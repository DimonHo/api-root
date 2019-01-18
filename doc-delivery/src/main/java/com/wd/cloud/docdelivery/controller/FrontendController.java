package com.wd.cloud.docdelivery.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.commons.constant.SessionConstant;
import com.wd.cloud.commons.dto.OrgDTO;
import com.wd.cloud.commons.dto.UserDTO;
import com.wd.cloud.commons.enums.StatusEnum;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.docdelivery.config.Global;
import com.wd.cloud.docdelivery.dto.HelpRecordDTO;
import com.wd.cloud.docdelivery.entity.HelpRecord;
import com.wd.cloud.docdelivery.entity.Permission;
import com.wd.cloud.docdelivery.feign.OrgServerApi;
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
import java.util.HashMap;
import java.util.Map;

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
    Global global;

    @Autowired
    FileService fileService;

    @Autowired
    MailService mailService;

    @Autowired
    FrontService frontService;

    @Autowired
    OrgServerApi orgServerApi;

    /**
     * 1. 文献求助
     *
     * @return
     */
    @ApiOperation(value = "文献求助")
    @PostMapping(value = "/help/form")
    public ResponseModel<HelpRecord> helpFrom(@Valid HelpRequestModel helpRequestModel, HttpServletRequest request) {
        UserDTO userDTO =(UserDTO) request.getSession().getAttribute(SessionConstant.LOGIN_USER);
        //获取邮箱
        String helperEmail = helpRequestModel.getHelperEmail();
        //获取求助总条数
        int helperEmailCount = frontService.findByHelperEmailCount(helperEmail);
        //获取今日总条数
        int helperEmialDay = frontService.findByHelperEmailDay(helperEmail);
        //获取权限
        Integer level = (Integer) request.getSession().getAttribute(SessionConstant.LEVEL);
        String finish = "文献求助记录已用完。";
        if (userDTO == null){
            //获取学校信息
            OrgDTO attribute1 =(OrgDTO) request.getSession().getAttribute(SessionConstant.IP_ORG);
            long orgId = attribute1.getId();
            Permission permission = frontService.getByLevelAndOrgId(level,orgId);
            //获取未登录每日能求助的条数
            Long todayTotal = permission.getTodayTotal();
            //获取未登录能求助的总条数
            Long total = permission.getTotal();
            if (todayTotal<=helperEmialDay || total<=helperEmailCount){
                return ResponseModel.ok().setMessage(finish);
            }
        }else {
            //获取学校信息
            OrgDTO org = userDTO.getOrg();
            long orgId = 0;
            if (org!= null){
                orgId = org.getId();
            }
            Permission permission = frontService.getByLevelAndOrgId(level,orgId);
            //获取每日能求助的条数
            Long todayTotal = permission.getTodayTotal();
            //获取能求助的总条数
            Long total = permission.getTotal();
            //校外登录
            if (level == 2 && org!=null && todayTotal<=helperEmialDay || total<=helperEmailCount){
                return ResponseModel.ok().setMessage(finish);
            }else if (level == 3 && todayTotal<=helperEmialDay || total<=helperEmailCount){//校内登录未验证
                return ResponseModel.ok().setMessage(finish);
            }else if (level == 6 || level == 7 && todayTotal<=helperEmialDay){//校外登录已验证,校内登录已验证
                return ResponseModel.ok().setMessage(finish);
            }else if (todayTotal<=helperEmialDay || total<=helperEmailCount){//校外第三方第一次登录
                return ResponseModel.ok().setMessage(finish);

            }
        }
        String ip = HttpUtil.getClientIP(request);
        HelpRecord helpRecord = new HelpRecord();
        String helpEmail = helpRequestModel.getHelperEmail();
        helpRecord.setHelpChannel(helpRequestModel.getHelpChannel());
        helpRecord.setHelperScid(helpRequestModel.getHelperScid());
        helpRecord.setHelperScname(helpRequestModel.getHelperScname());
        helpRecord.setHelperId(helpRequestModel.getHelperId());
        helpRecord.setHelperName(helpRequestModel.getHelperName());
        helpRecord.setRemark(helpRequestModel.getRemark());
        helpRecord.setAnonymous(helpRequestModel.isAnonymous());
        helpRecord.setHelperIp(ip);
        helpRecord.setHelperEmail(helpEmail);
        helpRecord.setSend(true);
        String msg = "waiting:文献求助已发送，应助结果将会在24h内发送至您的邮箱，请注意查收";
        msg = frontService.help(helpRecord, helpRequestModel.getDocTitle(), helpRequestModel.getDocHref());
        return ResponseModel.ok().setMessage(msg);
    }

    /**
     * 待应助列表
     *
     * @return
     */
    @ApiOperation(value = "待应助列表")
    @ApiImplicitParam(name = "helpChannel", value = "求助渠道，0:所有渠道，1：QQ,2:SPIS,3:智汇云，4：CRS", dataType = "Integer", paramType = "path")
    @GetMapping("/help/wait/{helpChannel}")
    public ResponseModel helpWaitList(@PathVariable int helpChannel,
                                      @PageableDefault(sort = {"gmtCreate"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<HelpRecordDTO> waitHelpRecords = frontService.getWaitHelpRecords(helpChannel, pageable);

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
    @ApiImplicitParam(name = "helpChannel", value = "求助渠道，0:所有渠道，1：QQ,2:SPIS,3:智汇云，4：CRS", dataType = "Integer", paramType = "path")
    @GetMapping("/help/finish/{helpChannel}")
    public ResponseModel helpSuccessList(@PathVariable Integer helpChannel,
                                         @PageableDefault(sort = {"gmtCreate"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<HelpRecordDTO> finishHelpRecords = frontService.getFinishHelpRecords(helpChannel, pageable);

        return ResponseModel.ok().setBody(finishHelpRecords);
    }

    @ApiOperation(value = "疑难文献列表")
    @ApiImplicitParam(name = "helpChannel", value = "求助渠道，0:所有渠道，1：QQ,2:SPIS,3:智汇云，4：CRS", dataType = "Integer", paramType = "path")
    @GetMapping("/help/failed/{helpChannel}")
    public ResponseModel helpFailedList(@PathVariable Integer helpChannel,
                                        @PageableDefault(sort = {"gmtCreate"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<HelpRecordDTO> finishHelpRecords = frontService.getFailedHelpRecords(helpChannel, pageable);

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
                                   @RequestParam(value = "status", required = false) Integer status,
                                   @PageableDefault(sort = {"gmtCreate"},
                                           direction = Sort.Direction.DESC) Pageable pageable) {
        Page<HelpRecordDTO> myHelpRecords = frontService.getHelpRecordsForUser(helperId, status, pageable);
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
        String ip = HttpUtil.getClientIP(request);
        HelpRecord helpRecord = frontService.give(helpRecordId, giverId, giverName, ip);
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
        String ip = HttpUtil.getClientIP(request);
        if (file == null) {
            return ResponseModel.fail(StatusEnum.DOC_FILE_EMPTY);
        } else if (!global.getFileTypes().contains(StrUtil.subAfter(file.getOriginalFilename(), ".", true))) {
            return ResponseModel.fail(StatusEnum.DOC_FILE_TYPE_ERROR);
        }
        // 检查求助记录状态是否为HelpStatusEnum.HELPING
        HelpRecord helpRecord = frontService.getHelpingRecord(helpRecordId);
        if (helpRecord == null) {
            return ResponseModel.fail(StatusEnum.DOC_HELP_NOT_FOUND);
        }
        frontService.uploadFile(helpRecord, giverId, file, ip);
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
        Page<HelpRecordDTO> helpRecords = frontService.getHelpRecordsForEmail(email, pageable);
        return ResponseModel.ok().setBody(helpRecords);
    }

    @ApiOperation(value = "邮箱或标题查询记录")
    @ApiImplicitParam(name = "keyword", value = "条件keyword", dataType = "String", paramType = "query")
    @GetMapping("/help/search")
    public ResponseModel recordsBySearch(@RequestParam String keyword, @PageableDefault(sort = {"gmtCreate"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<HelpRecordDTO> helpRecords = frontService.search(keyword, pageable);

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

    @ApiOperation(value = "下一个级别的求助上限")
    @GetMapping("/help/nextTotal")
    public ResponseModel getHelpFromCount(HttpServletRequest request) {
        String ip = HttpUtil.getClientIP(request);
        //String ip = "5431.152.1.230";
        ResponseModel<JSONObject> ipRang = orgServerApi.getByIp(ip);
        JSONObject body = ipRang.getBody();
        Map<String, Object> map = new HashMap<>();
        if (body == null) {

        } else {//校内访问
            Long id = body.getLong("id");
            int rule = 1;
            int nexRule = nexLevel(rule);
            //登录状态
            Permission permission = frontService.getOrgIdAndRule(id, nexRule);
            if (permission == null) {
                map.put("todayTotal", 20);
            } else {
                Long todayTotal = permission.getTodayTotal();
                map.put("todayTotal", todayTotal);
            }
        }
        return ResponseModel.ok().setBody(map);
    }


    private int nexLevel(int rule) {
        switch (rule) {
            default:
            case '0':
                rule = 2;
                break;
            case '1':
                rule = 3;
                break;
            case '2':
                rule = 6;
                break;
            case '3':
                rule = 7;
                break;

        }
        return rule;
    }


}
