package com.wd.cloud.wdtjserver.exception;

/**
 * @author He Zhigang
 * @date 2019/1/19
 * @Description:
 */
public enum ExceptionEnum {

    /**
     * 总求助上限
     */
    ORG_SERVER(1001801, "机构服务调用失败");
    private String message;
    private Integer status;

    private ExceptionEnum(Integer status, String message) {
        this.status = status;
        this.message = message;
    }

    public String message() {
        return message;
    }

    public Integer status() {
        return status;
    }
}
