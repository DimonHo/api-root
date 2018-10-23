package com.wd.cloud.commons.model;

import com.wd.cloud.commons.enums.StatusEnum;

import java.io.Serializable;

/**
 * @author He Zhigang
 * @date 2018/5/3
 * @remark api返回的response对象
 */
public class ResponseModel<T> implements Serializable {
    private Integer status;
    private String message;
    private T body;

    public ResponseModel() {
    }

    public ResponseModel(Throwable e) {
        this.message = e.toString();
        this.status = StatusEnum.FAIL.value();
    }

    public ResponseModel(StatusEnum statusEnum) {
        this.status = statusEnum.value();
        this.message = statusEnum.getMessage();
    }

    public Integer status() {
        return status;
    }

    public ResponseModel<T> status(Integer status) {
        this.status = status;
        return this;
    }

    public String message() {
        return message;
    }

    public ResponseModel<T> message(String message) {
        this.message = message;
        return this;
    }

    public T body() {
        return body;
    }

    public ResponseModel<T> body(T body) {
        this.body = body;
        return this;
    }


    public static ResponseModel ok(){
        return new ResponseModel(StatusEnum.SUCCESS);
    }

    public static ResponseModel ok(StatusEnum statusEnum){
        return new ResponseModel(statusEnum);
    }

    public static ResponseModel fail() {
        return new ResponseModel(StatusEnum.FAIL);
    }

    public static ResponseModel fail(StatusEnum statusEnum){
        return new ResponseModel(statusEnum);
    }

    public static ResponseModel fail(Throwable e){
        return new ResponseModel(e);
    }

    public static ResponseModel serverError() {
        return new ResponseModel(StatusEnum.INTERNAL_SERVER_ERROR);
    }


}
