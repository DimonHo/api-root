package com.wd.cloud.commons.model;

import com.wd.cloud.commons.enums.StatusEnum;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * @author He Zhigang
 * @date 2018/5/3
 * @remark api返回的response对象
 */
public class ResponseModel<T> implements Serializable {
    /**
     * 是否失败？error==true？失败：成功；
     */
    private boolean error;
    private Integer status;
    private String message;
    private T body;

    public ResponseModel() {
    }

    private ResponseModel(Throwable e) {
        this.error = true;
        this.message = e.toString();
        this.status = StatusEnum.FAIL.value();
    }

    private ResponseModel(StatusEnum statusEnum, boolean error) {
        this.error = error;
        this.status = statusEnum.value();
        this.message = statusEnum.getMessage();
    }

    public static ResponseModel ok() {
        return new ResponseModel(StatusEnum.SUCCESS, false);
    }

    public static ResponseModel ok(StatusEnum statusEnum) {
        return new ResponseModel(statusEnum, false);
    }

    /**
     * 请求失败，默认StatusEnum.FAIL
     *
     * @return
     */
    public static ResponseModel fail() {
        return new ResponseModel(StatusEnum.FAIL, true);
    }

    /**
     * 请求失败
     *
     * @param statusEnum
     * @return
     */
    public static ResponseModel fail(StatusEnum statusEnum) {
        return new ResponseModel(statusEnum, true);
    }

    public static ResponseModel fail(Throwable e) {
        return new ResponseModel(e);
    }

    public static ResponseModel serverError() {
        return new ResponseModel(StatusEnum.INTERNAL_SERVER_ERROR, true);
    }

    public boolean isError() {
        return error;
    }

    public ResponseModel<T> setError(boolean error) {
        this.error = error;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public ResponseModel<T> setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ResponseModel<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getBody() {
        return body;
    }

    public ResponseModel<T> setBody(T body) {
        this.body = body;
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ResponseModel.class.getSimpleName() + "[", "]")
                .add("error=" + error)
                .add("status=" + status)
                .add("message='" + message + "'")
                .add("body=" + body.toString())
                .toString();
    }
}
