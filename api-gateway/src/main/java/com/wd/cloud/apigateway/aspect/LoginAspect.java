package com.wd.cloud.apigateway.aspect;

import cn.hutool.http.HttpUtil;
import com.wd.cloud.apigateway.feign.OrgServerApi;
import com.wd.cloud.commons.constant.SessionConstant;
import com.wd.cloud.commons.model.ResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author He Zhigang
 * @date 2019/1/18
 * @Description:
 */
@Slf4j
@Aspect
@Component
public class LoginAspect {

    @Autowired
    OrgServerApi orgServerApi;

    @Pointcut("execution(public * com.wd.cloud.apigateway.controller.AuthController.login(..))")
    public void loginPoint() {
    }

    @Before("loginPoint()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();
        Boolean isOut = (Boolean) request.getSession().getAttribute(SessionConstant.IS_OUT);
        Integer level = (Integer) request.getSession().getAttribute(SessionConstant.LEVEL);
        if (isOut == null || level == null) {
            String clientIp = HttpUtil.getClientIP(request);
            ResponseModel orgResponse = orgServerApi.getByIp(clientIp);
            if (!orgResponse.isError()) {
                //非校外访问
                request.getSession().setAttribute(SessionConstant.IS_OUT, false);
                request.getSession().setAttribute(SessionConstant.LEVEL, 1);
                request.getSession().setAttribute(SessionConstant.IP_ORG, orgResponse.getBody());
                request.getSession().setAttribute(SessionConstant.ORG, orgResponse.getBody());
            } else {
                //校外访问
                request.getSession().setAttribute(SessionConstant.IS_OUT, true);
                request.getSession().setAttribute(SessionConstant.LEVEL, 0);
            }
        }
    }
}
