package com.wd.cloud.wdtjserver.controller;

import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.wdtjserver.entity.TjDaySetting;
import com.wd.cloud.wdtjserver.model.QuotaModel;
import com.wd.cloud.wdtjserver.service.TjService;
import com.wd.cloud.wdtjserver.utils.QuotaModelUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Time;
import java.text.SimpleDateFormat;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
@RestController
public class DaySettingController {

    @Autowired
    TjService tjService;

    @ApiOperation(value = "设置日基数", tags = {"后台设置"})
    @ApiImplicitParams ({
        @ApiImplicitParam(name = "orgId", value = "机构Id", dataType = "Long", paramType = "path")
    })
    @PostMapping("/setting/day/{orgId}")
    public ResponseModel add(@PathVariable Long orgId,
                             @RequestBody QuotaModel quotaModel) {
        TjDaySetting tjDaySetting = new TjDaySetting();
        tjDaySetting = QuotaModelUtils.quotaModel(orgId,quotaModel);
        tjDaySetting = tjService.save(tjDaySetting);
        return ResponseModel.ok().setBody(tjDaySetting);
    }




}
