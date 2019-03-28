package com.wd.cloud.docdelivery.interceptor;

import com.wd.cloud.commons.exception.AuthException;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: He Zhigang
 * @Date: 2019/3/25 9:40
 * @Description:
 */
public class LoginInteceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        AttributePrincipal principal = (AttributePrincipal) request.getUserPrincipal();
        if (principal == null){
            throw new AuthException();
        }
        return true;
    }
}
