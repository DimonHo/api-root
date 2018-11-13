package com.wd.cloud.wdtjserver.controller;

import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.wdtjserver.entity.TjDaySetting;
import com.wd.cloud.wdtjserver.model.QuotaModel;
import com.wd.cloud.wdtjserver.service.TjService;
import com.wd.cloud.wdtjserver.utils.ModelUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgId", value = "机构Id", dataType = "Long", paramType = "path")
    })
    @PostMapping("/setting/day/{orgId}")
    public ResponseModel add(@PathVariable Long orgId,
                             @RequestBody QuotaModel quotaModel) {
        TjDaySetting tjDaySetting = ModelUtil.build(quotaModel);
        tjDaySetting.setOrgId(orgId);
        return ResponseModel.ok().setBody(tjService.save(tjDaySetting));
    }

}
