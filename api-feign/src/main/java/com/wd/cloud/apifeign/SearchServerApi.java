package com.wd.cloud.apifeign;


import com.wd.cloud.commons.model.ResponseModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;


@FeignClient(value = "search-server")
public interface SearchServerApi {

    @GetMapping("/tj/minute")
    ResponseModel minuteTj(@RequestParam(value = "orgName", required = false) String orgName,
                           @RequestParam(value = "date", required = false) String date);


    @GetMapping("/tj/range")
    ResponseModel rangeTj(@RequestParam(value = "orgName", required = false) String orgName,
                          @RequestParam(value = "beginDate", required = false) String beginDate,
                          @RequestParam(value = "endDate", required = false) String endDate);


    @GetMapping(value = "/downloadsCount")
    ResponseModel downloadsCount(@RequestParam(value = "school") String school,
                                 @RequestParam(value = "date") String date);

}
