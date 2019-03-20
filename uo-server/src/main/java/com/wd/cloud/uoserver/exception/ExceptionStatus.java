package com.wd.cloud.uoserver.exception;

/**
 * @author He Zhigang
 * @date 2018/12/25
 * @Description:
 */
public class ExceptionStatus {

    public static final Integer UNDEFINED = 4000;

    public static final Integer NOT_FOUND = 404;

    public static final Integer NOT_ONE_RESULT = 4002;

    /**
     * IP不合法
     */
    public static final Integer NOT_IP = 1001101;

    /**
     * Ip已存在
     */
    public static final Integer EXISTS_IP = 1001102;
}
