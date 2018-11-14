package com.wd.cloud.wdtjserver.controller;

import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.wdtjserver.model.DateIntervalModel;
import com.wd.cloud.wdtjserver.model.HisQuotaModel;
import com.wd.cloud.wdtjserver.service.TjService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
@RestController
public class HisSettingController {

    @Autowired
    TjService tjService;

    @ApiOperation(value = "设置历史基数", tags = {"后台设置"})
    @ApiImplicitParam(name = "orgId", value = "机构Id", dataType = "Long", paramType = "path")
    @PostMapping("/setting/his/{orgId}")
    public ResponseModel add(@PathVariable Long orgId,
                             @RequestBody List<HisQuotaModel> hisQuotaModels) {
        List<DateIntervalModel> overlaps = tjService.saveTjHisSettings(orgId, hisQuotaModels);
        if (overlaps.size() > 0) {
            return ResponseModel.fail().setBody(overlaps);
        }
        return ResponseModel.ok();
    }

}
