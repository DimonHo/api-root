package com.wd.cloud.docdelivery.aspect;

import cn.hutool.core.util.StrUtil;
import com.wd.cloud.commons.constant.SessionConstant;
import com.wd.cloud.commons.exception.AuthException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: He Zhigang
 * @Date: 2019/3/23 9:41
 * @Description:
 */
@Slf4j
@Aspect
@Component
public class AuthAspect {
    @Autowired
    HttpServletRequest request;

    @Before(value = "@annotation(com.wd.cloud.commons.annotation.ValidateLogin)")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        String username = (String) request.getSession().getAttribute(SessionConstant.LOGIN_USER);
        if (StrUtil.isBlank(username)) {
            throw new AuthException();
        }
    }
}
