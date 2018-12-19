package com.wd.cloud.wdtjserver.feign;

import com.wd.cloud.commons.enums.StatusEnum;
import com.wd.cloud.commons.model.ResponseModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author He Zhigang
 * @date 2018/11/24
 * @Description:
 */
@FeignClient(value = "search-server", fallback = SearchServerApi.Fallback.class)
public interface SearchServerApi {

    @GetMapping("/tj/minute")
    ResponseModel minuteTj(@RequestParam(value = "orgName") String orgName,
                           @RequestParam(value = "date") String date);


    @GetMapping("/tj/range")
    ResponseModel rangeTj(@RequestParam(value = "orgName") String orgName,
                          @RequestParam(value = "beginDate") String beginDate,
                          @RequestParam(value = "endDate") String endDate);


    @GetMapping("/dc_count/name")
    ResponseModel dcCountByOrgName(@RequestParam(value = "orgName") String orgName,
                                   @RequestParam(value = "date") String date,
                                   @RequestParam(value = "type") Integer type);

    @Component
    class Fallback implements SearchServerApi {

        @Override
        public ResponseModel minuteTj(String orgName, String date) {
            return ResponseModel.fail(StatusEnum.FALL_BACK).setMessage("[fallback]:search-server调用失败！");
        }

        @Override
        public ResponseModel rangeTj(String orgName, String beginDate, String endDate) {
            return ResponseModel.fail(StatusEnum.FALL_BACK).setMessage("[fallback]:search-server调用失败！");
        }

        @Override
        public ResponseModel dcCountByOrgName(String orgName, String date, Integer type) {
            return ResponseModel.fail(StatusEnum.FALL_BACK).setMessage("[fallback]:search-server调用失败！");
        }
    }
}
