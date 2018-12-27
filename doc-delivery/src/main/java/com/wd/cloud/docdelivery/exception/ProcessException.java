package com.wd.cloud.docdelivery.exception;

import com.wd.cloud.commons.exception.ApiException;

/**
 * @author He Zhigang
 * @date 2018/12/27
 * @Description:
 */
public class ProcessException extends ApiException {

    public ProcessException(Integer status, String message) {
        super(status, message);
    }
}
