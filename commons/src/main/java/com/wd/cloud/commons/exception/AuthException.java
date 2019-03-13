package com.wd.cloud.commons.exception;

import com.wd.cloud.commons.enums.StatusEnum;

/**
 * @author He Zhigang
 * @date 2019/1/9
 * @Description: 认证/登陆异常
 */
public class AuthException extends ApiException {

    public AuthException() {
        super(StatusEnum.UNAUTHORIZED);
    }

    public AuthException(String message) {
        super(StatusEnum.UNAUTHORIZED.value(), message);
    }
}
