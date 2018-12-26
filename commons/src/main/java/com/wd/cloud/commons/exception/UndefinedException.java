package com.wd.cloud.commons.exception;

/**
 * @author He Zhigang
 * @date 2018/12/25
 * @Description:
 */
public class UndefinedException extends ApiException {

    public UndefinedException() {
        super(ExceptionCode.UNDEFINED, "服务器未知异常");
    }
}
