package com.wd.cloud.uoserver.exception;

import com.wd.cloud.commons.exception.ApiException;

/**
 * @author He Zhigang
 * @date 2018/12/25
 * @Description:
 */
public class NotFoundOrgException extends ApiException {

    public NotFoundOrgException(String message) {
        super(ExceptionStatus.NOT_FOUND, message);
    }

    public NotFoundOrgException() {
        super(ExceptionStatus.NOT_FOUND, "未找到机构信息");
    }

}
