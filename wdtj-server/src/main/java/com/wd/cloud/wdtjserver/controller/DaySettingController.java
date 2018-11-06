package com.wd.cloud.wdtjserver.controller;

import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.wdtjserver.model.QuotaModel;
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

    @PostMapping("/setting/day/{orgId}")
    public ResponseModel add(@PathVariable Long orgId,
                             @RequestBody QuotaModel quotaModel) {

        return ResponseModel.ok();

    }
}
