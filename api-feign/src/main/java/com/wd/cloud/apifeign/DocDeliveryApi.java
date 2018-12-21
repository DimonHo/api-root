package com.wd.cloud.apifeign;

import com.wd.cloud.commons.model.ResponseModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "doc-delivery")
public interface DocDeliveryApi {

    @GetMapping(value = "/ddc_count/name")
    ResponseModel ddcCountByOrgName(@RequestParam(value = "orgName", required = false) String orgName,
                          @RequestParam(value = "date", required = false) String date,
                          @RequestParam(value = "type", required = false, defaultValue = "1") Integer type);
}
