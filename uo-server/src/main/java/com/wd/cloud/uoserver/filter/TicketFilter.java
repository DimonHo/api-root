package com.wd.cloud.uoserver.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.wd.cloud.commons.constant.SessionConstant;
import com.wd.cloud.commons.enums.StatusEnum;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.commons.util.CasUtil;
import com.wd.cloud.commons.util.HttpUtil;
import com.wd.cloud.uoserver.config.ParameterRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jasig.cas.client.Protocol;
import org.jasig.cas.client.configuration.ConfigurationKeys;
import org.jasig.cas.client.util.AbstractCasFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author He Zhigang
 * @date 2019/2/23
 * @Description:
 */
@Slf4j
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
        String clientUrl = this.constructServiceUrl(request, response);
        if (cookieStr.contains("TGC=")) {
            log.info("clientUrl = {}", clientUrl);
            // 获取一个ST
            String st = CasUtil.getSt(requestWrapper, casServerLoginUrl, clientUrl, cookieStr);
            log.info("获取ST=====================[{}]", st);
            if (StrUtil.isNotBlank(st)) {
                //将ST加入到请求地址后面
                requestWrapper.addQueryString("ticket=" + st);
                filterChain.doFilter(requestWrapper, response);
            }
        }
        if (!clientUrl.contains("/login/info")){
            send(request,response);
        }
    }


    public void send(HttpServletRequest request,HttpServletResponse response) throws IOException {
        Boolean isOut = (Boolean) request.getSession().getAttribute(SessionConstant.IS_OUT);
        isOut = isOut == null ? true : isOut;
        request.getSession().setAttribute(SessionConstant.LEVEL, isOut ? 0 : 1);
        request.getSession().removeAttribute(SessionConstant.LOGIN_USER);
        response.getWriter().write(JSONUtil.toJsonStr(ResponseModel.fail(StatusEnum.UNAUTHORIZED)));
    }
}
