package com.wd.cloud.authserver.feign;

import cn.hutool.json.JSONObject;
import com.wd.cloud.commons.model.ResponseModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author He Zhigang
 * @date 2019/1/9
 * @Description:
 */
@FeignClient(value = "sso-server", url = "${cas.server-url-prefix}", fallback = SsoServerApi.Fallback.class)
public interface SsoServerApi {

    @PostMapping("/oauth/user/login")
    public ResponseModel<JSONObject> login(@RequestParam(name = "username") String username,
                                           @RequestParam(name = "password") String password);

    class Fallback implements SsoServerApi {

        @Override
        public ResponseModel<JSONObject> login(String username, String password) {
            return ResponseModel.fail();
        }
    }
}
