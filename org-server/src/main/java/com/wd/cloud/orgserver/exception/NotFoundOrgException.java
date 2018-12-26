package com.wd.cloud.orgserver.exception;

import com.wd.cloud.commons.exception.ApiException;

/**
 * @author He Zhigang
 * @date 2018/12/25
 * @Description:
 */
public class NotFoundOrgException extends ApiException {

    public NotFoundOrgException(String message) {
        super(ExceptionStatus.NOT_FOUND_ORG, message);
    }

    public NotFoundOrgException() {
        super(ExceptionStatus.NOT_FOUND_ORG, "未找到机构信息");
    }

}
