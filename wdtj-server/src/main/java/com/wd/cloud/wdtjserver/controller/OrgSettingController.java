package com.wd.cloud.wdtjserver.controller;

import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.wdtjserver.entity.TjOrg;
import com.wd.cloud.wdtjserver.service.TjService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
@RestController
public class OrgSettingController {

    @Autowired
    TjService tjService;

    @ApiOperation(value = "设置机构参数", tags = {"后台设置"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgId", value = "机构Id", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "showPv", value = "是否显示访问量", dataType = "boolean", paramType = "query"),
            @ApiImplicitParam(name = "showSc", value = "是否显示搜索量", dataType = "boolean", paramType = "query"),
            @ApiImplicitParam(name = "showDc", value = "是否显示下载量", dataType = "boolean", paramType = "query"),
            @ApiImplicitParam(name = "showDdc", value = "是否显示文献传递量", dataType = "boolean", paramType = "query"),
            @ApiImplicitParam(name = "showAvgTime", value = "是否显示平均访问时长", dataType = "boolean", paramType = "query")
    })
    @PostMapping("/setting/org/{orgId}")
    public ResponseModel<TjOrg> add(
            @PathVariable Long orgId,
            @RequestParam(required = false, defaultValue = "false") boolean showPv,
            @RequestParam(required = false, defaultValue = "false") boolean showSc,
            @RequestParam(required = false, defaultValue = "false") boolean showDc,
            @RequestParam(required = false, defaultValue = "false") boolean showDdc,
            @RequestParam(required = false, defaultValue = "false") boolean showAvgTime) {
        TjOrg tjOrg = new TjOrg();
        tjOrg.setOrgId(orgId).setShowPv(showPv).setShowSc(showSc).setShowDc(showDc).setShowDdc(showDdc).setShowAvgTime(showAvgTime);
        return ResponseModel.ok().setBody(tjService.save(tjOrg));
    }

    /**
     * 根据机构名称查询
     *
     * @param orgName
     * @return
     */
    @GetMapping("/setting/find")
    public ResponseModel<TjOrg> find(@RequestParam String orgName) {
        List<TjOrg> orgList = tjService.likeOrgName(orgName);
        return ResponseModel.ok().setBody(orgList);
    }

    /**
     * 根据指标过滤
     *
     * @param showPv
     * @param showSc
     * @param showDc
     * @param showDdc
     * @param showAvgTime
     * @return
     */
    @GetMapping("/setting/filter")
    public ResponseModel<TjOrg> find(@RequestParam(required = false, defaultValue = "false") boolean showPv,
                                     @RequestParam(required = false, defaultValue = "false") boolean showSc,
                                     @RequestParam(required = false, defaultValue = "false") boolean showDc,
                                     @RequestParam(required = false, defaultValue = "false") boolean showDdc,
                                     @RequestParam(required = false, defaultValue = "false") boolean showAvgTime) {
        return ResponseModel.ok().setBody(tjService.filterByQuota(showPv, showSc, showDc, showDdc, showAvgTime));
    }

}
