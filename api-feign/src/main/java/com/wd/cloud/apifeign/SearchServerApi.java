package com.wd.cloud.apifeign;


import com.wd.cloud.commons.model.ResponseModel;
import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;


@FeignClient(value = "search-server",fallback = SearchServerApi.Fallback.class)
public interface SearchServerApi {

    @GetMapping(value = "/indexVisit")
    List<Map<String, Object>> indexVisit(@RequestParam(value = "orgId") Long orgId,
                                         @RequestParam(value = "tjDate") String tjDate);


    @GetMapping(value = "/downloadsCount")
    ResponseModel downloadsCount(@RequestParam(value = "school") String school,
                                 @RequestParam(value = "date") String date);

    class Fallback implements SearchServerApi{


        @Override
        public List<Map<String, Object>> indexVisit(Long orgId, String tjDate) {
            return null;
        }

        @Override
        public ResponseModel downloadsCount(String school, String date) {
            return ResponseModel.fail();
        }
    }
}
