package com.wd.cloud.apigateway.filter;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.wd.cloud.commons.model.SessionKey;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * @author He Zhigang
 * @date 2018/5/10
 * @Description:
 */
public class AuthFilter extends ZuulFilter {
    private static final Log log = LogFactory.get();

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        Principal principal = request.getUserPrincipal();
        String userName = principal == null ? null : principal.getName();
        log.info("用户名：{}", userName);
        log.info("用户session:{}", request.getSession().getAttribute(SessionKey.LOGIN_USER));
        return null;
    }
}
