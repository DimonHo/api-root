package com.wd.cloud.authserver.service;

import com.wd.cloud.commons.dto.UserDTO;

/**
 * @author He Zhigang
 * @date 2018/6/5
 * @Description:
 */
public interface AuthService {

    UserDTO loing(String username, String pwd);
}
