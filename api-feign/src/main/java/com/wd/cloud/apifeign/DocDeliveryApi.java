package com.wd.cloud.apifeign;

import com.wd.cloud.commons.model.ResponseModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@FeignClient(value = "doc-delivery")
public interface DocDeliveryApi {

    @GetMapping("/front/help/count/org")
    ResponseModel getOrgHelpCount(@RequestParam(value = "orgId", required = false) Long orgId,
                                  @RequestParam(value = "orgName", required = false) String orgName,
                                  @RequestParam(value = "date", required = false) String date,
                                  @RequestParam(value = "type", required = false) Integer type);
}
