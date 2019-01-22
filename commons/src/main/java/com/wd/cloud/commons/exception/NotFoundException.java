package com.wd.cloud.commons.exception;

import com.wd.cloud.commons.enums.StatusEnum;

/**
 * @author He Zhigang
 * @date 2019/1/21
 * @Description:
 */
public class NotFoundException extends ApiException {

    public NotFoundException() {
        super(StatusEnum.NOT_FOUND);
    }

    public NotFoundException(String message) {
        super(StatusEnum.NOT_FOUND.value(), message);
    }
}
