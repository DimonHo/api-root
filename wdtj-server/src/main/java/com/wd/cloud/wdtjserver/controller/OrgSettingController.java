package com.wd.cloud.wdtjserver.controller;

import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.wdtjserver.entity.TjOrg;
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
