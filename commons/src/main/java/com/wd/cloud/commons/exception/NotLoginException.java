package com.wd.cloud.commons.exception;

/**
 * @author He Zhigang
 * @date 2019/1/16
 * @Description: 未登录
 */
public class NotLoginException extends ApiException {

    public NotLoginException() {
        super(ExceptionCode.NOT_LOGIN, "用户未登录");
    }
}
