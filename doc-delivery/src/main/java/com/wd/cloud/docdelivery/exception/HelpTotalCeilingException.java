package com.wd.cloud.docdelivery.exception;

import com.wd.cloud.commons.exception.ApiException;

/**
 * @author He Zhigang
 * @date 2019/1/19
 * @Description:
 */
public class HelpTotalCeilingException extends ApiException {

    public HelpTotalCeilingException(ExceptionEnum exceptionEnum) {
        super(exceptionEnum.getStatus(), exceptionEnum.getMessage());
    }
}
