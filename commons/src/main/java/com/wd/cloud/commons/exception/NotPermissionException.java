package com.wd.cloud.commons.exception;

import com.wd.cloud.commons.enums.StatusEnum;

/**
 * @author He Zhigang
 * @date 2019/3/6
 * @Description:
 */
public class NotPermissionException extends ApiException {

    public NotPermissionException() {
        super(StatusEnum.NOT_PERMISSION);
    }

    public NotPermissionException(String message) {
        super(StatusEnum.NOT_PERMISSION.value(), message);
    }
}
