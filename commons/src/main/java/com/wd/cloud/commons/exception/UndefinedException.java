package com.wd.cloud.commons.exception;

import com.wd.cloud.commons.enums.StatusEnum;

/**
 * @author He Zhigang
 * @date 2018/12/25
 * @Description:
 */
public class UndefinedException extends ApiException {

    public UndefinedException(Throwable e) {
        super(StatusEnum.UNKNOWN, e);
    }
}
