package com.wd.cloud.commons.exception;

import com.wd.cloud.commons.enums.StatusEnum;
import com.wd.cloud.commons.exception.ApiException;

/**
 * @Author: He Zhigang
 * @Date: 2019/4/18 14:51
 * @Description: 过期的异常
 */
public class ExpException extends ApiException {

    public ExpException() {
        super(StatusEnum.EXP_STATUS);
    }

    public ExpException(String message) {
        super(StatusEnum.EXP_STATUS.value(), message);
    }

}
