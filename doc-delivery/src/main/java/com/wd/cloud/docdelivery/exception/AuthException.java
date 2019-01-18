package com.wd.cloud.docdelivery.exception;

import com.wd.cloud.commons.enums.StatusEnum;
import com.wd.cloud.commons.exception.ApiException;

public class AuthException extends ApiException {
    public AuthException(){
        super(StatusEnum.UNAUTHORIZED.value(),"未登录");
    }

}
