package com.wd.cloud.commons.exception;

/**
 * @author He Zhigang
 * @date 2018/12/25
 * @Description:
 */
public class ApiException extends RuntimeException {

    protected Integer status;

    protected Object body;

    public ApiException(Integer status, String message, Object body, Throwable e) {
        super(message, e);
        this.status = status;
        this.body = body;
    }

    public ApiException(Integer status, String message, Object body) {
        this(status, message, body, null);
    }

    public ApiException(Integer status, String message) {
        this(status, message, null, null);
    }

    public ApiException(String message, Throwable e) {
        this(null, message, null, e);
    }

    public ApiException() {

    }

    public ApiException(Throwable e) {
        super(e);
    }

    public Integer getStatus() {
        return status;
    }

    public ApiException setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public Object getBody() {
        return body;
    }

    public ApiException setBody(Object body) {
        this.body = body;
        return this;
    }
}
