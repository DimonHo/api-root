package com.wd.cloud.wdtjserver.controller;

import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.wdtjserver.model.QuotaModel;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
@RestController
public class HisSettingController {

    @PostMapping("/setting/his/{orgId}")
    public ResponseModel add(@PathVariable Long orgId,
                             @RequestBody QuotaModel quotaModel,
                             @RequestParam Date beginDate,
                             @RequestParam Date endDate) {
        return ResponseModel.ok();
    }

    @PostMapping("/setting/his/{orgId}")
    public ResponseModel addFromMonth(@PathVariable Long orgId,
                                      @RequestBody QuotaModel quotaModel,
                                      @RequestParam Date month) {
        return ResponseModel.ok();
    }
}
