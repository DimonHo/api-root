package com.wd.cloud.apifeign;

import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.commons.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author He Zhigang
 * @date 2018/6/12
 * @Description:
 */
@FeignClient(value = "auth-server")
public interface AuthServerApi {

    /**
     * 获取用户信息
     *
     * @param userId
     * @return
     */
    @GetMapping("/user/info/{userId}")
    ResponseModel<UserDTO> getUserInfo(@PathVariable(value = "userId") Long userId);

}
