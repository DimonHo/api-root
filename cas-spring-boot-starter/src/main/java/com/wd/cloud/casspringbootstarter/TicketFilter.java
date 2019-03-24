package com.wd.cloud.casspringbootstarter;

import cn.hutool.core.util.StrUtil;
import com.wd.cloud.commons.util.CasUtil;
import com.wd.cloud.commons.util.HttpUtil;
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
            if (request.getUserPrincipal() == null){
                log.info("clientUrl = {}", clientUrl);
                // 获取一个ST
                String st = CasUtil.getSt(requestWrapper, casServerLoginUrl, clientUrl, cookieStr);
                log.info("获取ST=====================[{}]", st);
                if (StrUtil.isNotBlank(st)) {
                    //将ST加入到请求地址后面
                    requestWrapper.addQueryString("ticket=" + st);
                }
            }
        }else{
            request.getSession().removeAttribute(AbstractCasFilter.CONST_CAS_ASSERTION);
        }
        filterChain.doFilter(requestWrapper, response);
    }

}
