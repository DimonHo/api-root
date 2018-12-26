package com.wd.cloud.orgserver.exception;

import com.wd.cloud.commons.exception.ApiException;

/**
 * @author He Zhigang
 * @date 2018/12/25
 * @Description:
 */
public class NotOneOrgException extends ApiException {

    public NotOneOrgException(String message) {
        super(ExceptionStatus.NOT_ONE_RESULT, message);
    }

    public NotOneOrgException(Object body) {
        super(ExceptionStatus.NOT_ONE_RESULT, "找到了多个机构信息", body);
    }

    public NotOneOrgException() {
        super(ExceptionStatus.NOT_ONE_RESULT, "找到了多个机构信息");
    }
}
