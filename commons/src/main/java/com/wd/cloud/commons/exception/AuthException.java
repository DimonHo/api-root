package com.wd.cloud.commons.exception;

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
