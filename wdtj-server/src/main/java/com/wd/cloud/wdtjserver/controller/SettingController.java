package com.wd.cloud.wdtjserver.controller;

import com.wd.cloud.commons.enums.StatusEnum;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.wdtjserver.entity.TjOrg;
import com.wd.cloud.wdtjserver.service.SettingService;
import com.wd.cloud.wdtjserver.service.TjService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
@RestController
public class SettingController {

    @Autowired
    SettingService settingService;

    @ApiOperation(value = "设置机构参数", tags = {"后台设置"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgId", value = "机构Id", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "showPv", value = "是否显示访问量", dataType = "boolean", paramType = "query"),
            @ApiImplicitParam(name = "showSc", value = "是否显示搜索量", dataType = "boolean", paramType = "query"),
            @ApiImplicitParam(name = "showDc", value = "是否显示下载量", dataType = "boolean", paramType = "query"),
            @ApiImplicitParam(name = "showDdc", value = "是否显示文献传递量", dataType = "boolean", paramType = "query"),
            @ApiImplicitParam(name = "showAvgTime", value = "是否显示平均访问时长", dataType = "boolean", paramType = "query")
    })
    @PostMapping("/org/{orgId}")
    public ResponseModel add(
            @PathVariable Long orgId,
            @RequestParam String createUser,
            @RequestParam(required = false, defaultValue = "false") boolean showPv,
            @RequestParam(required = false, defaultValue = "false") boolean showSc,
            @RequestParam(required = false, defaultValue = "false") boolean showDc,
            @RequestParam(required = false, defaultValue = "false") boolean showDdc,
            @RequestParam(required = false, defaultValue = "false") boolean showAvgTime) {
        TjOrg tjOrg = settingService.saveTjOrg(orgId, showPv, showSc, showDc, showDdc, showAvgTime,createUser);
        if (tjOrg != null) {
            return ResponseModel.ok().setBody(tjOrg);
        }
        return ResponseModel.fail(StatusEnum.NOT_FOUND);
    }

    @ApiOperation(value = "禁用/解除禁用某机构", tags = {"后台设置"})
    @ApiImplicitParam(name = "orgId", value = "机构Id", dataType = "String", paramType = "path")
    @PatchMapping("/org/{orgId}")
    public ResponseModel forbadeOrg(@PathVariable Long orgId) {
        TjOrg tjOrg = settingService.forbade(orgId);
        if (tjOrg != null) {
            return ResponseModel.ok().setBody(tjOrg);
        }
        return ResponseModel.fail(StatusEnum.NOT_FOUND);
    }


    @ApiOperation(value = "机构名称或操作人模糊查询", tags = {"后台设置"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", value = "查询条件", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "history", value = "是否是历史记录", dataType = "Boolean", paramType = "query")
    })
    @GetMapping("/org/find")
    public ResponseModel<Page> find(@RequestParam(required = false) String query,
                                    @RequestParam(required = false) Boolean history,
                                    @PageableDefault(sort = {"orgName"}, direction = Sort.Direction.ASC) Pageable pageable) {
        Page<TjOrg> orgPage = settingService.likeQuery(query, history, pageable);
        return ResponseModel.ok().setBody(orgPage);
    }


    @ApiOperation(value = "获取所有已生效的机构列表", tags = {"后台设置"})
    @GetMapping("/org/all")
    public ResponseModel<Page> all(@PageableDefault(sort = {"gmtModified"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TjOrg> orgEnableList = settingService.getEnabledFromAll(pageable);
        return ResponseModel.ok().setBody(orgEnableList);
    }


    @ApiOperation(value = "获取所有历史的机构列表", tags = {"后台设置"})
    @GetMapping("/org/his")
    public ResponseModel<Page> history(@PageableDefault(sort = {"gmtModified"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TjOrg> orgHisList = settingService.getHistoryFromAll(pageable);
        return ResponseModel.ok().setBody(orgHisList);
    }


    @ApiOperation(value = "根据指标过滤机构设置列表", tags = {"后台设置"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "showPv", value = "是否有访问量指标", dataType = "Boolean", paramType = "query"),
            @ApiImplicitParam(name = "showSc", value = "是否有搜索量指标", dataType = "Boolean", paramType = "query"),
            @ApiImplicitParam(name = "showDc", value = "是否有下载量指标", dataType = "Boolean", paramType = "query"),
            @ApiImplicitParam(name = "showDdc", value = "是否有文献传递量指标", dataType = "Boolean", paramType = "query"),
            @ApiImplicitParam(name = "showAvgTime", value = "是否有平均访问时长指标", dataType = "Boolean", paramType = "query"),
            @ApiImplicitParam(name = "forbade", value = "是否禁用", dataType = "Boolean", paramType = "query")
    })
    @GetMapping("/org/filter")
    public ResponseModel<TjOrg> find(@RequestParam(required = false) Boolean showPv,
                                     @RequestParam(required = false) Boolean showSc,
                                     @RequestParam(required = false) Boolean showDc,
                                     @RequestParam(required = false) Boolean showDdc,
                                     @RequestParam(required = false) Boolean showAvgTime,
                                     @RequestParam(required = false) Boolean forbade,
                                     @PageableDefault(sort = {"gmtModified"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseModel.ok().setBody(settingService.filterOrgByQuota(showPv, showSc, showDc, showDdc, showAvgTime, forbade, pageable));
    }


}
