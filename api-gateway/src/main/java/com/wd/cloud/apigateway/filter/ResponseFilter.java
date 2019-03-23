package com.wd.cloud.apigateway.filter;

import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONObject;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.wd.cloud.commons.constant.SessionConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

/**
 * @author He Zhigang
 * @date 2018/10/10
 * @Description:
 */
@Slf4j
@Component
public class ResponseFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return FilterConstants.POST_TYPE;
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
        RequestContext ctx = RequestContext.getCurrentContext();
        //如果是文件下载，此处会读取文件流导致客户端读取的文件流不完整而出现下载文件损坏。
        //InputStream bodyStream = RequestContext.getCurrentContext().getResponseDataStream();
        Integer level = (Integer) ctx.getRequest().getSession().getAttribute(SessionConstant.LEVEL);
        log.info("响应头中加入用户level信息:{}", level);
        String username = (String) ctx.getRequest().getSession().getAttribute(SessionConstant.LOGIN_USER);
        JSONObject org = (JSONObject) ctx.getRequest().getSession().getAttribute(SessionConstant.ORG);
        JSONObject user = new JSONObject();
        if (org!=null){
            user.putAll(org);
        }
        if (username != null) {
            user.put("username",username);
            log.info("响应头中加入登陆用户信息:{}", username);
        }
        ctx.getResponse().setHeader("level", level + "");
        ctx.getResponse().setHeader("user", URLUtil.encode(user.toString()));
        return null;
    }

}
