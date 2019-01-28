package com.wd.cloud.apigateway.filter;

import cn.hutool.http.HttpUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.wd.cloud.apigateway.feign.OrgServerApi;
import com.wd.cloud.commons.constant.SessionConstant;
import com.wd.cloud.commons.dto.OrgDTO;
import com.wd.cloud.commons.model.ResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author He Zhigang
 * @date 2018/12/20
 * @Description:
 */
@Slf4j
@Component
public class ClientIpFilter extends ZuulFilter {

    @Autowired
    OrgServerApi orgServerApi;

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        Boolean isOut = (Boolean) request.getSession().getAttribute(SessionConstant.IS_OUT);
        Integer level = (Integer) request.getSession().getAttribute(SessionConstant.LEVEL);
        if (isOut == null || level == null) {
            String clientIp = HttpUtil.getClientIP(request);
            ResponseModel<OrgDTO> orgResponse = orgServerApi.getByIp(clientIp);
            if (!orgResponse.isError()) {
                //非校外访问
                request.getSession().setAttribute(SessionConstant.IS_OUT, false);
                request.getSession().setAttribute(SessionConstant.LEVEL, 1);
                request.getSession().setAttribute(SessionConstant.IP_ORG, orgResponse.getBody());
                request.getSession().setAttribute(SessionConstant.ORG, orgResponse.getBody());
            } else {
                //校外访问
                request.getSession().setAttribute(SessionConstant.IS_OUT, true);
                request.getSession().setAttribute(SessionConstant.LEVEL, 0);
            }
        }
        return null;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

}