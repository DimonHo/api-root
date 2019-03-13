package com.wd.cloud.uoserver.exception;

import com.wd.cloud.commons.exception.ApiException;

/**
 * @author He Zhigang
 * @date 2018/12/25
 * @Description:
 */
public class NotFoundUserException extends ApiException {

    public NotFoundUserException(String message) {
        super(ExceptionStatus.NOT_FOUND, message);
    }

    public NotFoundUserException() {
        super(ExceptionStatus.NOT_FOUND, "未找到用户信息");
    }

}
