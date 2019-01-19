package com.wd.cloud.docdelivery.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.wd.cloud.commons.constant.SessionConstant;
import com.wd.cloud.commons.dto.OrgDTO;
import com.wd.cloud.commons.dto.UserDTO;
import com.wd.cloud.commons.enums.StatusEnum;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.docdelivery.config.Global;
import com.wd.cloud.docdelivery.dto.GiveRecordDTO;
import com.wd.cloud.docdelivery.dto.HelpRecordDTO;
import com.wd.cloud.docdelivery.entity.HelpRecord;
import com.wd.cloud.docdelivery.entity.Literature;
import com.wd.cloud.docdelivery.entity.Permission;
import com.wd.cloud.docdelivery.exception.AuthException;
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
import java.util.List;
import java.util.Map;

/**
 * @author He Zhigang
 * @date 2018/5/3
 */
@Api(value = "前台controller", tags = {"前台文献互助接口"})
@RestController
@RequestMapping("/front")
public class FrontendController {

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

    @Autowired
    HttpServletRequest request;

    @ApiOperation(value = "文献求助")
    @PostMapping(value = "/help/form")
    public ResponseModel<HelpRecord> helpFrom(@Valid HelpRequestModel helpRequestModel, HttpServletRequest request) {
        UserDTO userDTO = (UserDTO) request.getSession().getAttribute(SessionConstant.LOGIN_USER);

        String ip = HttpUtil.getClientIP(request);
        HelpRecord helpRecord = new HelpRecord();
        Literature literature = new Literature();
        BeanUtil.copyProperties(helpRequestModel, helpRecord);
        BeanUtil.copyProperties(helpRequestModel, literature);

        OrgDTO orgDTO = (OrgDTO) request.getSession().getAttribute(SessionConstant.IP_ORG);
        if (userDTO != null) {
            helpRecord.setHelperId(userDTO.getId());
            helpRecord.setHelperName(userDTO.getUsername());
            if (userDTO.getOrg() != null) {
                orgDTO = userDTO.getOrg();
            }
        }
        if (orgDTO != null) {
            helpRecord.setHelperScid(orgDTO.getId());
            helpRecord.setHelperScname(orgDTO.getName());
        }
        helpRecord.setHelperIp(ip);
        helpRecord.setSend(true);
        String msg = frontService.help(helpRecord, literature);
        return ResponseModel.ok().setMessage(msg);
    }

    @ApiOperation(value = "查询求助记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "channel", value = "求助渠道，0:paper平台，1：QQ,2:SPIS,3:智汇云，4：CRS", dataType = "List", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "过滤状态，0：待应助， 1：应助中（用户已认领，15分钟内上传文件）， 2: 待审核（用户已应助）， 3：求助第三方（第三方应助）， 4：应助成功（审核通过或管理员应助）， 5：应助失败（超过15天无结果）", dataType = "List", paramType = "query"),
            @ApiImplicitParam(name = "keyword", value = "模糊查询", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "email", value = "邮箱过滤", dataType = "String", paramType = "query")
    })
    @GetMapping("/help/records")
    public ResponseModel helpRecords(@RequestParam(required = false) List<Integer> channel,
                                     @RequestParam(required = false) List<Integer> status,
                                     @RequestParam(required = false) String keyword,
                                     @RequestParam(required = false) String email,
                                     @PageableDefault(sort = {"gmtCreate"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<HelpRecordDTO> helpRecordDTOS = frontService.getHelpRecords(channel, status, email, keyword, pageable);
        return ResponseModel.ok().setBody(helpRecordDTOS);
    }

    @ApiOperation(value = "待应助列表")
    @ApiImplicitParam(name = "channel", value = "求助渠道，0:paper平台，1：QQ,2:SPIS,3:智汇云，4：CRS", dataType = "Integer", paramType = "query")
    @GetMapping("/help/records/wait")
    public ResponseModel helpWaitList(@RequestParam(required = false) List<Integer> channel,
                                      @PageableDefault(sort = {"gmtCreate"}, direction = Sort.Direction.DESC) Pageable pageable) {

        Page<HelpRecordDTO> waitHelpRecords = frontService.getWaitHelpRecords(channel, pageable);

        return ResponseModel.ok().setBody(waitHelpRecords);
    }


    @ApiOperation(value = "求助完成列表")
    @ApiImplicitParam(name = "channel", value = "求助渠道，0:paper平台，1：QQ,2:SPIS,3:智汇云，4：CRS", dataType = "Integer", paramType = "query")
    @GetMapping("/help/records/finish")
    public ResponseModel helpFinishList(@RequestParam(required = false) List<Integer> channel,
                                        @PageableDefault(sort = {"gmtCreate"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<HelpRecordDTO> finishHelpRecords = frontService.getFinishHelpRecords(channel, pageable);

        return ResponseModel.ok().setBody(finishHelpRecords);
    }


    @ApiOperation(value = "求助成功列表")
    @ApiImplicitParam(name = "channel", value = "求助渠道，0:paper平台，1：QQ,2:SPIS,3:智汇云，4：CRS", dataType = "Integer", paramType = "query")
    @GetMapping("/help/records/success")
    public ResponseModel helpSuccessList(@RequestParam(required = false) List<Integer> channel,
                                         @PageableDefault(sort = {"gmtCreate"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<HelpRecordDTO> successHelpRecords = frontService.getSuccessHelpRecords(channel, pageable);
        return ResponseModel.ok().setBody(successHelpRecords);
    }

    @ApiOperation(value = "疑难文献列表")
    @ApiImplicitParam(name = "channel", value = "求助渠道，0:paper平台，1：QQ,2:SPIS,3:智汇云，4：CRS", dataType = "Integer", paramType = "query")
    @GetMapping("/help/records/failed")
    public ResponseModel helpFailedList(@RequestParam(required = false) List<Integer> channel,
                                        @PageableDefault(sort = {"gmtCreate"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<HelpRecordDTO> finishHelpRecords = frontService.getFailedHelpRecords(channel, pageable);

        return ResponseModel.ok().setBody(finishHelpRecords);
    }


    @ApiOperation(value = "我的求助记录")
    @ApiImplicitParam(name = "status", value = "状态", dataType = "List", paramType = "query")
    @GetMapping("/help/records/my")
    public ResponseModel myHelpRecords(@RequestParam(required = false) List<Integer> status,
                                       @PageableDefault(sort = {"gmtCreate"}, direction = Sort.Direction.DESC) Pageable pageable) {

        Page<HelpRecordDTO> myHelpRecords = frontService.myHelpRecords(status, pageable);
        return ResponseModel.ok().setBody(myHelpRecords);
    }

    @ApiOperation(value = "我的应助记录")
    @ApiImplicitParam(name = "status", value = "状态", dataType = "List", paramType = "query")
    @GetMapping("/give/records/my")
    public ResponseModel myGiveRecords(@RequestParam(required = false) List<Integer> status,
                                       @PageableDefault(sort = {"gmtCreate"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<GiveRecordDTO> myGiveRecords = frontService.myGiveRecords(status, pageable);
        return ResponseModel.ok().setBody(myGiveRecords);
    }


    @ApiOperation(value = "应助认领")
    @ApiImplicitParam(name = "helpRecordId", value = "求助记录ID", dataType = "Long", paramType = "path")
    @PatchMapping("/help/records/{helpRecordId}/giving")
    public ResponseModel giving(@PathVariable Long helpRecordId) {
        String ip = HttpUtil.getClientIP(request);
        UserDTO userDTO = (UserDTO) request.getSession().getAttribute(SessionConstant.LOGIN_USER);
        if (userDTO == null) {
            throw new AuthException();
        }
        HelpRecord helpRecord = frontService.give(helpRecordId, userDTO.getId(), userDTO.getUsername(), ip);
        return ResponseModel.ok().setBody(helpRecord);
    }


    @ApiOperation(value = "应助取消")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "helpRecordId", value = "求助记录ID", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "giverId", value = "应助者用户ID", dataType = "Long", paramType = "query")
    })
    @PatchMapping("/help/records/{helpRecordId}/givin/cancel")
    public ResponseModel cancelGiving(@PathVariable Long helpRecordId) {
        UserDTO userDTO = (UserDTO) request.getSession().getAttribute(SessionConstant.LOGIN_USER);
        if (userDTO == null) {
            throw new AuthException();
        }
        //检查用户是否已经认领了应助
        String docTtitle = frontService.checkExistsGiveing(userDTO.getId());
        if (docTtitle != null) {
            //有认领记录，可以取消
            frontService.cancelGivingHelp(helpRecordId, userDTO.getId());
            return ResponseModel.ok();
        }
        return ResponseModel.fail(StatusEnum.NOT_FOUND);
    }


    @ApiOperation(value = "应助上传文件")
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


    @ApiOperation(value = "获取用户当天已求助记录的数量")
    @ApiImplicitParam(name = "email", value = "用户邮箱", dataType = "String", paramType = "query")
    @GetMapping("/help/count")
    public ResponseModel getUserHelpCountToDay(@RequestParam String email) {

        return ResponseModel.ok().setBody(frontService.getCountHelpRecordToDay(email));
    }

    @ApiOperation(value = "下一个级别的求助上限")
    @GetMapping("/help/nextLevel")
    public ResponseModel getHelpFromCount(HttpServletRequest request) {
        Integer level = (Integer) request.getSession().getAttribute(SessionConstant.LEVEL);
        OrgDTO orgDTO = (OrgDTO) request.getSession().getAttribute(SessionConstant.ORG);

        Permission permission = frontService.nextLevel(orgDTO.getId(), level);
        Map<String, Long> resp = new HashMap<>();
        //登录状态
        if (permission == null) {
            resp.put("todayTotal", 10L);
        } else {
            Long todayTotal = permission.getTodayTotal();
            resp.put("todayTotal", todayTotal);
        }
        return ResponseModel.ok().setBody(resp);
    }


}
