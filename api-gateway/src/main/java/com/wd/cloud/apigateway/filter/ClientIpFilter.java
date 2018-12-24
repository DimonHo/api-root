package com.wd.cloud.apigateway.filter;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author He Zhigang
 * @date 2018/12/20
 * @Description:
 */
@Component
public class ClientIpFilter extends ZuulFilter {
    private static final Log log = LogFactory.get();
    private static final String CLIENT_IP = "CLIENT_IP";

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String remoteAddr = getIpAddr(request);
        log.info("真实IP={}", remoteAddr);
        ctx.getZuulRequestHeaders().put(CLIENT_IP, remoteAddr);
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

    /**
     * 获取登录用户IP地址
     *
     * @param request
     * @return
     */
    private static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        //Nginx
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        } else if (ip.contains(",")) {
            log.info("使用了代理的IP:" + ip);
            String[] ips = ip.split(",");
            int i = 0;
            while (i < ips.length) {
                if (ips[i] != null && ips[i].length() != 0 && !"unknown".equalsIgnoreCase(ips[i])) {
                    ip = ips[i];
                    break;
                }
                i++;
            }
        }
        return ip;
    }
}