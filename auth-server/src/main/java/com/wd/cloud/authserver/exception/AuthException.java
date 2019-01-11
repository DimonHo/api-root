package com.wd.cloud.authserver.exception;

import com.wd.cloud.commons.exception.ApiException;

/**
 * @author He Zhigang
 * @date 2019/1/9
 * @Description: 认证异常
 */
public class AuthException extends ApiException {

    public AuthException(Integer status, String message) {
        super(status, message);
    }
}
