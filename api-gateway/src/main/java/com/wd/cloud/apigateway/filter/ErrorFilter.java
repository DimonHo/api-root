package com.wd.cloud.apigateway.filter;

import cn.hutool.json.JSONUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.wd.cloud.commons.exception.ApiException;
import com.wd.cloud.commons.model.ResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

/**
 * @author He Zhigang
 * @date 2018/10/25
 * @Description:
 */
@Slf4j
@Component
public class ErrorFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return FilterConstants.ERROR_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.SEND_ERROR_FILTER_ORDER - 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        Throwable cause = ctx.getThrowable();
        ResponseModel responseModel = new ResponseModel();
        if (cause instanceof ApiException) {
            responseModel.setStatus(((ApiException) cause).getStatus());
            responseModel.setBody(((ApiException) cause).getBody());
            responseModel.setError(true);
        }
        RequestContext.getCurrentContext().setResponseBody(JSONUtil.toJsonStr(responseModel));
        return null;
    }
}
