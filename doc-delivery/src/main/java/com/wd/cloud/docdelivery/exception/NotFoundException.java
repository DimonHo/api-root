package com.wd.cloud.docdelivery.exception;

import com.wd.cloud.commons.exception.ApiException;

/**
 * @author He Zhigang
 * @date 2018/12/26
 * @Description:
 */
public class NotFoundException extends ApiException {

    public NotFoundException(String message) {
        super(ExceptionCode.NOT_FOUND, message);
    }
}
