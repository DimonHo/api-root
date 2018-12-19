package com.wd.cloud.apifeign;


import com.wd.cloud.commons.model.ResponseModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(value = "search-server")
public interface SearchServerApi {

    @GetMapping("/tj/minute")
    ResponseModel minuteTj(@RequestParam(value = "orgName") String orgName,
                           @RequestParam(value = "date") String date);


    @GetMapping("/tj/range")
    ResponseModel rangeTj(@RequestParam(value = "orgName") String orgName,
                          @RequestParam(value = "beginDate") String beginDate,
                          @RequestParam(value = "endDate") String endDate);


    @GetMapping(value = "/dc_count/name")
    ResponseModel dcCountByOrgName(@RequestParam(value = "orgName") String orgName,
                          @RequestParam(value = "date") String date,
                          @RequestParam(value = "type") Integer type);

}
