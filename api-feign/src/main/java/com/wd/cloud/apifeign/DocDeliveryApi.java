package com.wd.cloud.apifeign;

import com.wd.cloud.commons.model.ResponseModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "doc-delivery",fallback = DocServerApi.Fallback.class)
public interface DocServerApi {
    @GetMapping("/front/deliveryCount")
    ResponseModel deliveryCount(@RequestParam(value = "school") String school,
                                @RequestParam(value = "date") String date);


    class Fallback implements DocServerApi{

        @Override
        public ResponseModel deliveryCount(String school, String date) {
            return ResponseModel.fail();
        }
    }
}
