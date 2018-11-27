package com.wd.cloud.wdtjserver.feign;

import com.wd.cloud.commons.enums.StatusEnum;
import com.wd.cloud.commons.model.ResponseModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

/**
 * @author He Zhigang
 * @date 2018/11/24
 * @Description:
 */
@FeignClient(value = "search-server", fallback = SearchServerApi.Fallback.class)
public interface SearchServerApi extends com.wd.cloud.apifeign.SearchServerApi {

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
        public ResponseModel downloadsCount(String school, String date) {
            return ResponseModel.fail(StatusEnum.FALL_BACK).setMessage("[fallback]:search-server调用失败！").setBody(0);
        }
    }
}
