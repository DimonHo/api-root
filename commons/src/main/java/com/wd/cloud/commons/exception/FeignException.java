package com.wd.cloud.commons.exception;

/**
 * @author He Zhigang
 * @date 2018/12/25
 * @Description:
 */
public class FeignException extends ApiException {

    public FeignException(String message) {
        super(ExceptionCode.FEIGN_EXCEPTION, "[" + message + "]服务调用失败");
    }
}
