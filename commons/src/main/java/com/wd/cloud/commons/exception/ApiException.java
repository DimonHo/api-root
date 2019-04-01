package com.wd.cloud.commons.exception;

import com.wd.cloud.commons.enums.StatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author He Zhigang
 * @date 2018/12/25
 * @Description:
 */

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ApiException extends RuntimeException {
    static final long serialVersionUID = 1L;

    protected Integer status;

    protected Object body;

    public ApiException() {
    }

    public ApiException(StatusEnum statusEnum) {
        this(statusEnum.value(), statusEnum.message(), null);
    }

    public ApiException(StatusEnum statusEnum, Throwable e) {
        this(statusEnum.value(), statusEnum.message(), null, e);
    }

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

    public ApiException(Throwable e) {
        super(e);
    }

}
