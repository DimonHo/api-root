package com.wd.cloud.wdtjserver.feign;

import com.wd.cloud.commons.enums.StatusEnum;
import com.wd.cloud.commons.model.ResponseModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author He Zhigang
 * @date 2018/11/24
 * @Description:
 */
@FeignClient(value = "doc-delivery", fallback = DocDeliveryApi.Fallback.class)
public interface DocDeliveryApi extends com.wd.cloud.apifeign.DocDeliveryApi {

    @Component
    class Fallback implements DocDeliveryApi {

        @Override
        public ResponseModel getOrgHelpCount(Long orgId, String orgName, Date date, Integer type) {
            return ResponseModel.fail(StatusEnum.FALL_BACK).setMessage("[fallback]:doc-delivery调用失败！").setBody(0);
        }
    }
}
