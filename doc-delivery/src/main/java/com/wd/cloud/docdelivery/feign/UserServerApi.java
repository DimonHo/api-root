package com.wd.cloud.docdelivery.feign;

import cn.hutool.json.JSONObject;
import com.wd.cloud.commons.dto.UserDTO;
import com.wd.cloud.commons.enums.StatusEnum;
import com.wd.cloud.commons.model.ResponseModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author He Zhigang
 * @date 2019/1/28
 * @Description:
 */
@FeignClient(value = "user-server", fallback = UserServerApi.Fallback.class)
public interface UserServerApi {

    @GetMapping("/user/info")
    ResponseModel<UserDTO> getUserInfo(@RequestParam(value = "username") String username);

    @Component
    class Fallback implements UserServerApi {

        @Override
        public ResponseModel<UserDTO> getUserInfo(String ip) {
            return ResponseModel.fail(StatusEnum.FALL_BACK).setMessage("[fallback]:user-server调用失败！");
        }
    }
}
