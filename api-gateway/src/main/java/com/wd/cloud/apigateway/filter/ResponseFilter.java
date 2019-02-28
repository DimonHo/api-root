package com.wd.cloud.apigateway.filter;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.wd.cloud.commons.constant.SessionConstant;
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
        //如果是文件下载，此处会读取文件流导致客户端读取的文件流不完整而出现下载文件损坏。
        //InputStream bodyStream = RequestContext.getCurrentContext().getResponseDataStream();
        Integer levle = (Integer) RequestContext.getCurrentContext().getRequest().getSession().getAttribute(SessionConstant.LEVEL);
        RequestContext.getCurrentContext().getResponse().setHeader("level", levle + "");
        return null;
    }

}
