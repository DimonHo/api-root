package com.wd.cloud.apifeign;


import com.wd.cloud.commons.model.ResponseModel;
import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;


@FeignClient(value = "search-server")
public interface SearchServerApi {

    @GetMapping(value = "/indexVisit")
    ResponseModel indexVisit(@RequestParam(value = "orgId") Long orgId,
                                         @RequestParam(value = "tjDate") String tjDate);


    @GetMapping(value = "/downloadsCount")
    ResponseModel downloadsCount(@RequestParam(value = "school") String school,
                                 @RequestParam(value = "date") String date);

}
