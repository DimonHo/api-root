package com.wd.cloud.docdelivery.exception;

import com.wd.cloud.commons.exception.ApiException;

/**
 * @author He Zhigang
 * @date 2018/12/26
 * @Description:
 */
public class HelpException extends ApiException {

    public HelpException(Integer status,String message){
        super(status,message);
    }
}
