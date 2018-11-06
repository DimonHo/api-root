package com.wd.cloud.wdtjserver.controller;

import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.wdtjserver.model.QuotaModel;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
@RestController
public class HisSettingController {

    @ApiOperation(value = "设置历史基数", tags = {"后台设置"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgId", value = "机构Id", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "beginDate", value = "开始时间", dataType = "Date", paramType = "query"),
            @ApiImplicitParam(name = "endDate", value = "结束时间", dataType = "Date", paramType = "query")
    })
    @PostMapping("/setting/his/{orgId}")
    public ResponseModel add(@PathVariable Long orgId,
                             @RequestBody QuotaModel quotaModel,
                             @RequestParam Date beginDate,
                             @RequestParam Date endDate) {
        return ResponseModel.ok();
    }

    @ApiOperation(value = "按月设置历史基数", tags = {"后台设置"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgId", value = "机构Id", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "month", value = "月份", dataType = "Date", paramType = "path")
    })
    @PostMapping("/setting/his/{orgId}/{month}")
    public ResponseModel addFromMonth(@PathVariable Long orgId,
                                      @RequestBody QuotaModel quotaModel,
                                      @PathVariable Date month) {
        return ResponseModel.ok();
    }
}
