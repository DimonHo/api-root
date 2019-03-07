package com.wd.cloud.apigateway.filter;

import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.wd.cloud.commons.constant.SessionConstant;
import com.wd.cloud.commons.dto.UserDTO;
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
        ctx.getResponse().setHeader("Access-Control-Allow-Headers", "*");
        ctx.getResponse().setHeader("Access-Control-Expose-Headers", "*");
        //如果是文件下载，此处会读取文件流导致客户端读取的文件流不完整而出现下载文件损坏。
        //InputStream bodyStream = RequestContext.getCurrentContext().getResponseDataStream();
        Integer level = (Integer) ctx.getRequest().getSession().getAttribute(SessionConstant.LEVEL);
        log.info("响应头中加入用户level信息:{}", level);
        UserDTO userDTO = (UserDTO) ctx.getRequest().getSession().getAttribute(SessionConstant.LOGIN_USER);
        ctx.getResponse().setHeader("level", level + "");
        if (userDTO != null) {
            log.info("响应头中加入登陆用户信息:{}", userDTO.toString());
            ctx.getResponse().setHeader("user", URLUtil.encode(JSONUtil.toJsonStr(userDTO)));
        }
        return null;
    }

}
