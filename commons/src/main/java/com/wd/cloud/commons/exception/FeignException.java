package com.wd.cloud.commons.exception;

import com.wd.cloud.commons.enums.StatusEnum;

/**
 * @author He Zhigang
 * @date 2018/12/25
 * @Description:
 */
public class FeignException extends ApiException {

    public FeignException() {
        super(StatusEnum.FALL_BACK);
    }

    public FeignException(String message) {
        super(StatusEnum.FALL_BACK.value(), "[" + message + "]服务调用失败");
    }
}
