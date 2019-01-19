package com.wd.cloud.apigateway.feign;

import cn.hutool.json.JSONObject;
import com.wd.cloud.commons.enums.StatusEnum;
import com.wd.cloud.commons.model.ResponseModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author He Zhigang
 * @date 2019/1/19
 * @Description:
 */
@FeignClient(value = "user-server", fallback = UserServerApi.Fallback.class)
public interface UserServerApi {

    @GetMapping("/user/info")
    public ResponseModel<JSONObject> getUserInfo(@RequestParam(value = "userId") Long userId);

    @Component
    class Fallback implements UserServerApi {
        @Override
        public ResponseModel<JSONObject> getUserInfo(Long userId) {
            return ResponseModel.fail(StatusEnum.FALL_BACK);
        }
    }
}
