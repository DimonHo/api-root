package com.wd.cloud.apigateway.filter;

import cn.hutool.core.lang.Console;
import cn.hutool.json.JSONUtil;
import com.wd.cloud.apigateway.config.ParameterRequestWrapper;
import com.wd.cloud.apigateway.utils.CasUtil;
import com.wd.cloud.apigateway.utils.HttpUtil;
import com.wd.cloud.commons.exception.AuthException;
import com.wd.cloud.commons.model.ResponseModel;
import org.jasig.cas.client.Protocol;
import org.jasig.cas.client.configuration.ConfigurationKeys;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author He Zhigang
 * @date 2019/2/23
 * @Description:
 */
public class TicketFilter extends AbstractCasFilter {

    private String casServerLoginUrl;

    public TicketFilter() {
        this(Protocol.CAS2);
    }

    protected TicketFilter(Protocol protocol) {
        super(protocol);
    }

    @Override
    public void init() {
        super.init();
        this.casServerLoginUrl = this.getString(ConfigurationKeys.CAS_SERVER_LOGIN_URL);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        ParameterRequestWrapper requestWrapper = new ParameterRequestWrapper(request);
        String cookieStr = HttpUtil.getCookieStr(requestWrapper);
        response.setContentType("application/json");
        if (cookieStr != null && cookieStr.contains("TGC=")) {
            String clientUrl = this.constructServiceUrl(request, response);
            // 获取一个ST
            String st = CasUtil.getSt(requestWrapper, casServerLoginUrl, clientUrl, cookieStr);
            if (st != null) {
                //将ST加入到请求地址后面
                requestWrapper.addQueryString("ticket="+st);
                filterChain.doFilter(requestWrapper, response);
            }
        }
    }
}
