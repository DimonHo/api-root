package com.wd.cloud.commons.model;

import cn.hutool.json.JSONUtil;
import com.wd.cloud.commons.enums.StatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * @author He Zhigang
 * @date 2018/5/3
 * @remark api返回的response对象
 */
@Data
@Accessors(chain = true)
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
        this.message = statusEnum.message();
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


    @Override
    public String toString() {
        StringJoiner responseModel = new StringJoiner(", ", ResponseModel.class.getSimpleName() + "[", "]");
        responseModel.add("error=" + error);
        if (status != null) {
            responseModel.add("status=" + status);
        }
        if (message != null) {
            responseModel.add("message='" + message + "'");
        }
        if (body != null) {
            responseModel.add("body=" + JSONUtil.toJsonStr(body));
        }
        return responseModel.toString();
    }
}
