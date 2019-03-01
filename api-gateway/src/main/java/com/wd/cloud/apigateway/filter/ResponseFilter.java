package com.wd.cloud.apigateway.filter;

import cn.hutool.json.JSONUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.wd.cloud.commons.constant.SessionConstant;
import com.wd.cloud.commons.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
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
        Integer level = (Integer) RequestContext.getCurrentContext().getRequest().getSession().getAttribute(SessionConstant.LEVEL);
        UserDTO userDTO = (UserDTO) RequestContext.getCurrentContext().getRequest().getSession().getAttribute(SessionConstant.LOGIN_USER);
        RequestContext.getCurrentContext().getResponse().setHeader("level", level + "");
        if (userDTO != null) {
            RequestContext.getCurrentContext().getResponse().setHeader("user", JSONUtil.toJsonStr(userDTO));
        }
        return null;
    }

}
