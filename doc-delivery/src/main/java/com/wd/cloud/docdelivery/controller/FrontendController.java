package com.wd.cloud.docdelivery.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.commons.constant.SessionConstant;
import com.wd.cloud.commons.enums.StatusEnum;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.commons.util.FileUtil;
import com.wd.cloud.docdelivery.config.GlobalConfig;
import com.wd.cloud.docdelivery.entity.DocFile;
import com.wd.cloud.docdelivery.entity.GiveRecord;
import com.wd.cloud.docdelivery.entity.HelpRecord;
import com.wd.cloud.docdelivery.entity.Literature;
import com.wd.cloud.docdelivery.enums.GiveTypeEnum;
import com.wd.cloud.docdelivery.enums.HelpStatusEnum;
import com.wd.cloud.docdelivery.feign.FsServerApi;
import com.wd.cloud.docdelivery.model.HelpRequestModel;
import com.wd.cloud.docdelivery.service.FileService;
import com.wd.cloud.docdelivery.service.FrontService;
import com.wd.cloud.docdelivery.service.MailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;

/**
 * @author He Zhigang
 * @date 2018/5/3
 */
@Api(value = "前台controller", tags = {"前台文献互助接口"})
@RestController
@RequestMapping("/front")
public class FrontendController {

    private static final Log log = LogFactory.get();
    @Autowired
    GlobalConfig globalConfig;

    @Autowired
    FileService fileService;

    @Autowired
    MailService mailService;

    @Autowired
    FrontService frontService;

    @Autowired
    FsServerApi fsServerApi;

    /**
     * 1. 文献求助
     *
     * @return
     */
    @ApiOperation(value = "文献求助")
    @PostMapping(value = "/help/form")
    public ResponseModel<HelpRecord> helpFrom(@Valid HelpRequestModel helpRequestModel, HttpServletRequest request) {

        HelpRecord helpRecord = new HelpRecord();
        String helpEmail = helpRequestModel.getHelperEmail();
        helpRecord.setHelpChannel(helpRequestModel.getHelpChannel());
        helpRecord.setHelperScid(helpRequestModel.getHelperScid());
        helpRecord.setHelperScname(helpRequestModel.getHelperScname());
        helpRecord.setHelperId(helpRequestModel.getHelperId());
        helpRecord.setHelperName(helpRequestModel.getHelperName());
        helpRecord.setHelperIp(request.getLocalAddr());
        helpRecord.setHelperEmail(helpEmail);
        log.info("用户:[{}]正在求助文献:[{}]",helpEmail,helpRequestModel.getDocTitle());
        Literature literature = new Literature();
        // 防止调用者传过来的docTitle包含HTML标签，在这里将标签去掉
        literature.setDocTitle(frontService.clearHtml(helpRequestModel.getDocTitle().trim()));
        literature.setDocHref(helpRequestModel.getDocHref().trim());
        // 先查询元数据是否存在
        Literature literatureData = frontService.queryLiterature(literature);
        String msg = "waiting:文献求助已发送，应助结果将会在24h内发送至您的邮箱，请注意查收";
        if (null == literatureData) {
            // 如果不存在，则新增一条元数据
            literatureData = frontService.saveLiterature(literature);
        }
        if (frontService.checkExists(helpEmail, literatureData)) {
            return ResponseModel
                    .fail(StatusEnum.DOC_HELP_REPEATED)
                    .setMessage("error:您最近15天内已求助过这篇文献,请注意查收邮箱");
        }
        helpRecord.setLiterature(literatureData);
        DocFile docFile = frontService.getReusingFile(literatureData);
        // 如果文件已存在，自动应助成功
        if (null != docFile) {
            helpRecord.setStatus(HelpStatusEnum.HELP_SUCCESSED.getCode());
            GiveRecord giveRecord = new GiveRecord();
            giveRecord.setDocFile(docFile);
            giveRecord.setGiverType(GiveTypeEnum.AUTO.getCode());
            giveRecord.setGiverName("自动应助");
            //先保存求助记录，得到求助ID，再关联应助记录
            helpRecord = frontService.saveHelpRecord(helpRecord);
            giveRecord.setHelpRecord(helpRecord);
            frontService.saveGiveRecord(giveRecord);
            String downloadUrl = fileService.getDownloadUrl(helpRecord.getId());
            mailService.sendMail(helpRecord.getHelpChannel(),
                    helpRequestModel.getHelperScname(),
                    helpEmail,
                    helpRecord.getLiterature().getDocTitle(),
                    downloadUrl,
                    HelpStatusEnum.HELP_SUCCESSED);
            msg = "success:文献求助成功,请登陆邮箱" + helpEmail + "查收结果";
        } else {
            try {
                // 发送通知邮件
                mailService.sendNotifyMail(helpRecord.getHelpChannel(), helpRequestModel.getHelperScname(), helpRequestModel.getHelperEmail());
                // 保存求助记录
                frontService.saveHelpRecord(helpRecord);
            } catch (Exception e) {
                return ResponseModel
                        .fail(StatusEnum.DB_PRIMARY_EXCEPTION)
                        .setMessage(msg);
            }
        }
        return ResponseModel.ok().setMessage(msg);
    }

    /**
     * 待应助列表
     *
     * @return
     */
    @ApiOperation(value = "待应助列表")
    @ApiImplicitParam(name = "helpChannel", value = "求助渠道", dataType = "Integer", paramType = "path")
    @GetMapping("/help/wait/{helpChannel}")
    public ResponseModel helpWaitList(@PathVariable int helpChannel,
                                      @PageableDefault(sort = {"gmtCreate"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<HelpRecord> waitHelpRecords = frontService.getWaitHelpRecords(helpChannel, pageable);
        return ResponseModel.ok().setBody(waitHelpRecords);
    }

    /**
     * 应助完成列表，包含成功和失败的
     *
     * @param helpChannel
     * @param pageable
     * @return
     */
    @ApiOperation(value = "求助完成列表")
    @ApiImplicitParam(name = "helpChannel", value = "求助渠道", dataType = "Integer", paramType = "path")
    @GetMapping("/help/finish/{helpChannel}")
    public ResponseModel helpSuccessList(@PathVariable Integer helpChannel,
                                         @PageableDefault(sort = {"gmtCreate"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<HelpRecord> finishHelpRecords = frontService.getFinishHelpRecords(helpChannel, pageable);
        return ResponseModel.ok().setBody(finishHelpRecords);
    }

    /**
     * 4. 我的求助
     *
     * @param helperId
     * @return
     */
    @ApiOperation(value = "我的求助记录")
    @ApiImplicitParam(name = "helperId", value = "用户ID", dataType = "Long", paramType = "path")
    @GetMapping("/help/records/{helperId}")
    public ResponseModel myRecords(@PathVariable Long helperId,
                                   @PageableDefault(sort = {"gmtCreate"},
                                           direction = Sort.Direction.DESC) Pageable pageable) {
        Page<HelpRecord> myHelpRecords = frontService.getHelpRecordsForUser(helperId, pageable);
        return ResponseModel.ok().setBody(myHelpRecords);
    }


    /**
     * 应助认领
     *
     * @param helpRecordId
     * @param giverId
     * @return
     */
    @ApiOperation(value = "我要应助")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "helpRecordId", value = "求助记录ID", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "giverId", value = "应助者用户ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "giverName", value = "应助者用户名称", dataType = "String", paramType = "query")
    })
    @PatchMapping("/give/{helpRecordId}")
    public ResponseModel giving(@PathVariable Long helpRecordId,
                                @RequestParam Long giverId,
                                @RequestParam String giverName,
                                HttpServletRequest request) {
        HelpRecord helpRecord = frontService.getWaitOrThirdHelpRecord(helpRecordId);
        // 该求助记录状态为非待应助，那么可能已经被其他人应助过或已应助完成
        if (helpRecord == null) {
            return ResponseModel
                    .fail(StatusEnum.DOC_OTHER_GIVING);
        }
        //检查用户是否已经认领了应助
        String docTitle = frontService.checkExistsGiveing(giverId);
        if (docTitle != null) {
            return ResponseModel
                    .fail(StatusEnum.DOC_FINISH_GIVING)
                    .setMessage("请先完成您正在应助的文献:" + docTitle);
        }
        helpRecord = frontService.givingHelp(helpRecordId, giverId, giverName, HttpUtil.getClientIP(request));
        return ResponseModel.ok().setBody(helpRecord);
    }

    /**
     * 取消应助
     *
     * @param helpRecordId
     * @return
     */
    @ApiOperation(value = "取消应助")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "helpRecordId", value = "求助记录ID", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "giverId", value = "应助者用户ID", dataType = "Long", paramType = "query")
    })
    @PatchMapping("/give/cancle/{helpRecordId}")
    public ResponseModel cancelGiving(@PathVariable Long helpRecordId,
                                      @RequestParam Long giverId) {
        //检查用户是否已经认领了应助
        String docTtitle = frontService.checkExistsGiveing(giverId);
        if (docTtitle != null) {
            //有认领记录，可以取消
            frontService.cancelGivingHelp(helpRecordId, giverId);
            return ResponseModel.ok();
        }
        return ResponseModel.fail(StatusEnum.NOT_FOUND);
    }

    /**
     * 我来应助，上传文件
     *
     * @param file
     * @return
     */
    @ApiOperation(value = "用户上传应助文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "helpRecordId", value = "求助记录ID", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "giverId", value = "应助者用户ID", dataType = "Long", paramType = "query")
    })
    @PostMapping("/give/upload/{helpRecordId}")
    public ResponseModel upload(@PathVariable Long helpRecordId,
                                @RequestParam Long giverId,
                                @NotNull MultipartFile file,
                                HttpServletRequest request) {
        if (file == null) {
            return ResponseModel.fail(StatusEnum.DOC_FILE_EMPTY);
        } else if (!globalConfig.getFileTypes().contains(StrUtil.subAfter(file.getOriginalFilename(), ".", true))) {
            return ResponseModel.fail(StatusEnum.DOC_FILE_TYPE_ERROR);
        }
        // 检查求助记录状态是否为HelpStatusEnum.HELPING
        HelpRecord helpRecord = frontService.getHelpingRecord(helpRecordId);
        if (helpRecord == null) {
            return ResponseModel.fail(StatusEnum.DOC_HELP_NOT_FOUND);
        }
        String fileId = null;
        try {
            String fileMd5 = FileUtil.fileMd5(file.getInputStream());
            ResponseModel<JSONObject> checkResult = fsServerApi.checkFile(globalConfig.getHbaseTableName(), fileMd5);
            if (!checkResult.isError() && checkResult.getBody() != null) {
                log.info("文件已存在，秒传成功！");
                fileId = checkResult.getBody().getStr("fileId");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (StrUtil.isBlank(fileId)) {
            //保存文件
            ResponseModel<JSONObject> responseModel = fsServerApi.uploadFile(globalConfig.getHbaseTableName(), file);
            if (responseModel.isError()) {
                log.error("文件服务调用失败：{}", responseModel.getMessage());
                return ResponseModel.fail().setMessage("文件上传失败，请重试");
            }
            fileId = responseModel.getBody().getStr("fileId");
        }
        String fileName = file.getOriginalFilename();
        DocFile docFile = frontService.saveDocFile(helpRecord.getLiterature(), fileId, fileName);
        //更新记录
        frontService.createGiveRecord(helpRecord, giverId, docFile, HttpUtil.getClientIP(request));
        return ResponseModel.ok().setMessage("应助成功，感谢您的帮助");
    }


    /**
     * 指定邮箱的求助记录
     *
     * @param email
     * @return
     */
    @ApiOperation(value = "根据邮箱查询求助记录")
    @ApiImplicitParam(name = "email", value = "条件email", dataType = "String", paramType = "query")
    @GetMapping("/help/records")
    public ResponseModel recordsByEmail(@RequestParam String email,
                                        @PageableDefault(sort = {"gmtCreate"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<HelpRecord> helpRecords = frontService.getHelpRecordsForEmail(email, pageable);
        return ResponseModel.ok().setBody(helpRecords);
    }

    @ApiOperation(value = "邮箱或标题查询记录")
    @ApiImplicitParam(name = "keyword", value = "条件keyword", dataType = "String", paramType = "query")
    @GetMapping("/help/search")
    public ResponseModel recordsBySearch(@RequestParam String keyword, @PageableDefault(sort = {"gmtCreate"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<HelpRecord> helpRecords = frontService.search(keyword, pageable);

        return ResponseModel.ok().setBody(helpRecords);
    }

    /**
     * 文献求助记录
     *
     * @return
     */

    @GetMapping("/help/records/all")
    public ResponseModel allRecords(@PageableDefault(sort = {"gmtCreate"}, direction = Sort.Direction.DESC) Pageable pageable, HttpServletRequest request) {
        return ResponseModel.ok().setBody(request.getSession().getAttribute(SessionConstant.LOGIN_USER));
    }


    @ApiOperation(value = "获取用户当天已求助记录的数量")
    @ApiImplicitParam(name = "email", value = "用户邮箱", dataType = "String", paramType = "query")
    @GetMapping("/help/count")
    public ResponseModel getUserHelpCountToDay(@RequestParam String email) {

        return ResponseModel.ok().setBody(frontService.getCountHelpRecordToDay(email));
    }

    @ApiOperation(value = "聚合统计求助记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgId", value = "机构ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "orgName", value = "机构名称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "date", value = "当前时间", dataType = "Date", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "统计类型0：按分钟统计，1：按小时统计，2按天统计，3，按月统计，4：按年统计", dataType = "Integer", paramType = "query")

    })
    @GetMapping("/help/count/org")
    public ResponseModel getOrgHelpCountToMinute(@RequestParam(required = false) Long orgId,
                                                 @RequestParam(required = false) String orgName,
                                                 @RequestParam(required = false) String date,
                                                 @RequestParam(required = false, defaultValue = "0") Integer type) {
        if (orgId == null && orgName == null) {
            return ResponseModel.fail(StatusEnum.PAYMENT_REQUIRED).setMessage("机构id和机构名称不能同时为空！");
        }
        return ResponseModel.ok().setBody(frontService.getCountByOrg(orgId, orgName, date, type));
    }

}
