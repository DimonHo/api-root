package com.wd.cloud.apigateway.filter;

import cn.hutool.json.JSONObject;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.stereotype.Component;

/**
 * @author He Zhigang
 * @date 2018/10/25
 * @Description:
 */
@Component
public class ErrorFilter  extends ZuulFilter {
    private static final Log log = LogFactory.get();

    @Override
    public String filterType() {
        return "error";
    }

    @Override
    public int filterOrder() {
        return 100;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        JSONObject responseModel = new JSONObject();
        responseModel.put("error", true);
        responseModel.put("status", -1);
        responseModel.put("message", "未知错误！！！");


        RequestContext.getCurrentContext().setResponseBody(responseModel.toString());
        return null;
    }
}
