package com.wd.cloud.wdtjserver.controller;

import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.wdtjserver.entity.TjOrg;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
@RestController
public class OrgSettingController {

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
        return ResponseModel.ok();
    }

}