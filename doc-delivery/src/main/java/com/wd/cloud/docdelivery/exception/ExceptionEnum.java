package com.wd.cloud.docdelivery.exception;

/**
 * @author He Zhigang
 * @date 2019/1/19
 * @Description:
 */
public enum ExceptionEnum {

    /**
     * 总求助上限
     */
    HELP_TOTAL_CEILING(599,"求助已达上限"),
    HELP_TOTAL_TODAY_CEILING(598,"今日求助已达上限");

    private String message;
    private Integer status;

    private ExceptionEnum(Integer status,String message){
        this.status = status;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Integer getStatus() {
        return status;
    }
}
