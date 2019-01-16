package com.wd.cloud.apigateway.filter;

import cn.hutool.json.JSONObject;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.wd.cloud.commons.constant.SessionConstant;
import com.wd.cloud.commons.dto.UserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * @author He Zhigang
 * @date 2018/5/10
 * @Description:
 */
@Component
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
        String url = RequestContext.getCurrentContext().getRequest().getRequestURI();
        //需要权限校验URL
        if ("/auth-server/auth/index".equalsIgnoreCase(url)) {
            return true;
        }
        return false;
    }

    @Override
    public Object run() throws ZuulException {
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        Principal principal = request.getUserPrincipal();
        String userName = principal == null ? null : principal.getName();
        log.info("用户名：{}", userName);
        UserDTO userInfo = (UserDTO) request.getSession().getAttribute(SessionConstant.LOGIN_USER);
        if (userInfo != null) {
            log.info("用户session:{}", userInfo.getUsername());
        } else {
            // 过滤该请求，不对其进行路由
            RequestContext.getCurrentContext().setSendZuulResponse(false);
            //返回错误代码
            RequestContext.getCurrentContext().setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
            JSONObject responseModel = new JSONObject();
            responseModel.put("error", true);
            responseModel.put("status", 401);
            responseModel.put("message", "无权限访问");
            RequestContext.getCurrentContext().setResponseBody(responseModel.toString());
        }
        return null;
    }
}
