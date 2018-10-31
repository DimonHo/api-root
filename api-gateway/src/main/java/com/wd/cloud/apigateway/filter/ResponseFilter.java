package com.wd.cloud.apigateway.filter;

import cn.hutool.core.io.IoUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * @author He Zhigang
 * @date 2018/10/10
 * @Description:
 */
@Component
public class ResponseFilter extends ZuulFilter {
    private static final Log log = LogFactory.get();

    @Override
    public String filterType() {
        return "post";
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
        InputStream stream = RequestContext.getCurrentContext().getResponseDataStream();
        String body = IoUtil.read(stream, "UTF-8");
        log.info("http响应::> {}", body);
        RequestContext.getCurrentContext().setResponseBody(body);
        return null;
    }

}
