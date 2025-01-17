package com.wd.cloud.wdtjserver.controller;

import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.wdtjserver.model.ViewDataModel;
import com.wd.cloud.wdtjserver.service.ViewService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Descriptiwon:
 */
@RestController
@RequestMapping("/view")
public class ViewController {
    @Autowired
    ViewService viewService;

    @ApiOperation(value = "获取机构统计数据", tags = {"数据展示"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgFlag", value = "机构Id", dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "beginTime", value = "开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "viewType", value = "展示类型：0按分钟，1按时，2按天，3按月，4按年", dataType = "Integer", paramType = "query")
    })
    @GetMapping("/{orgFlag}")
    public ResponseModel getData(@PathVariable String orgFlag,
                                 @RequestParam String beginTime,
                                 @RequestParam String endTime,
                                 @RequestParam(required = false, defaultValue = "0") int viewType) {
        ViewDataModel viewDataModel = viewService.getViewDate(orgFlag, beginTime, endTime, viewType);
        if (viewDataModel == null) {
            return ResponseModel.fail().setMessage("该机构不存在或没有权限查看");
        }
        return ResponseModel.ok().setBody(viewDataModel);
    }
}
