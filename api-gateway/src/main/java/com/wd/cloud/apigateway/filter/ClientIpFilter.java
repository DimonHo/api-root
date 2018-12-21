package com.wd.cloud.apigateway.filter;

import cn.hutool.http.HttpUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author He Zhigang
 * @date 2018/12/20
 * @Description:
 */
@Component
public class ClientIpFilter extends ZuulFilter {
    private static final String CLIENT_IP = "CLIENT_IP";

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String remoteAddr = HttpUtil.getClientIP(request);
        ctx.getZuulRequestHeaders().put(CLIENT_IP, remoteAddr);
        return null;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }
}