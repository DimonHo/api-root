package com.wd.cloud.authserver.service;

import com.wd.cloud.authserver.dto.UserInfoDTO;

/**
 * @author He Zhigang
 * @date 2018/6/5
 * @Description:
 */
public interface AuthService {

    UserInfoDTO loing(String username, String pwd);
}
