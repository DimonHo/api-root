package com.wd.cloud.apigateway.filter;

import cn.hutool.core.io.IoUtil;
import cn.hutool.http.Method;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.stereotype.Component;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;

/**
 * @author He Zhigang
 * @date 2018/10/10
 * @Description:
 */
@Component
public class RequestFilter extends ZuulFilter {
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
        final RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest req = ctx.getRequest();
        log.info("http请求::{} {}:{}", req.getScheme(), req.getRemoteAddr(), req.getRemotePort());
        StringBuilder params = new StringBuilder("?");
        Enumeration<String> names = req.getParameterNames();
        if (Method.GET.name().equals(req.getMethod())) {
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                params.append(name);
                params.append("=");
                params.append(req.getParameter(name));
                params.append("&");
            }
        }
        if (params.length() > 0) {
            params.delete(params.length() - 1, params.length());
        }
        log.info("http请求:: > {} {} {} {}", req.getMethod(), req.getRequestURI(), params, req.getProtocol());
        Enumeration<String> headers = req.getHeaderNames();
        while (headers.hasMoreElements()) {
            String name = (String) headers.nextElement();
            String value = req.getHeader(name);
            log.info("http请求头信息:: > {}:{}", name, value);
        }
        if (!ctx.isChunkedRequestBody()) {
            ServletInputStream inp = null;
            try {
                inp = ctx.getRequest().getInputStream();
                if (inp != null) {
                    String body = IoUtil.read(inp, "utf-8");
                    log.info("http请求body:: > {}", body);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //doSomething();
        return null;
    }

    private void doSomething() {
        System.out.println("dosometing===========================================");
        throw new RuntimeException("Exist some errors...");
    }
}
