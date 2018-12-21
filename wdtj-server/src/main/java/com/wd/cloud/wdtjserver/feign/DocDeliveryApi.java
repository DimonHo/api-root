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
@FeignClient(value = "doc-delivery", fallback = DocDeliveryApi.Fallback.class)
public interface DocDeliveryApi {

    @GetMapping(value = "/ddc_count/name")
    ResponseModel ddcCountByOrgName(@RequestParam(value = "orgName", required = false) String orgName,
                                    @RequestParam(value = "date", required = false) String date,
                                    @RequestParam(value = "type", required = false, defaultValue = "1") Integer type);

    @Component
    class Fallback implements DocDeliveryApi {

        @Override
        public ResponseModel ddcCountByOrgName(String orgName, String date, Integer type) {
            return ResponseModel.fail(StatusEnum.FALL_BACK).setMessage("[fallback]:doc-delivery调用失败！");
        }
    }
}
