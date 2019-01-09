package com.wd.cloud.docdelivery.exception;

public class AuthException extends RuntimeException{
    public AuthException(){
        super("未登录");
    }

}
