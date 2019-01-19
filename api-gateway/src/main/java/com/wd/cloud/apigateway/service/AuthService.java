package com.wd.cloud.apigateway.service;

import com.wd.cloud.commons.dto.UserDTO;

/**
 * @author He Zhigang
 * @date 2019/1/19
 * @Description:
 */
public interface AuthService {

    UserDTO loing(String username, String pwd);
}
