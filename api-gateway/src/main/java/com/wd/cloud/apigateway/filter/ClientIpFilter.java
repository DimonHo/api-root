package com.wd.cloud.apigateway.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.useragent.UserAgentUtil;
import cn.hutool.json.JSONObject;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.wd.cloud.apigateway.feign.UoServerApi;
import com.wd.cloud.commons.constant.SessionConstant;
import com.wd.cloud.commons.enums.ClientType;
import com.wd.cloud.commons.model.ResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.Assertion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
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
    UoServerApi uoServerApi;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    RedisOperationsSessionRepository redisOperationsSessionRepository;

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        Assertion principal = (Assertion) request.getSession().getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION);
        // 如果用户已登录
        if (principal != null) {
            String casUser = principal.getPrincipal().getName();
            String sessionUser = (String) request.getSession().getAttribute(SessionConstant.LOGIN_USER);
            JSONObject sessionOrg = (JSONObject) request.getSession().getAttribute(SessionConstant.ORG);
            // session中已存在用户信息，则跳过
            if (casUser.equals(sessionUser) && sessionOrg != null) {
                return null;
            }
            // session Key
            String sessionKey;
            //如果是移动端
            if (UserAgentUtil.parse(request.getHeader(Header.USER_AGENT.name())).getBrowser().isMobile()) {
                request.getSession().setAttribute(SessionConstant.CLIENT_TYPE, ClientType.MOBILE);
                sessionKey = casUser + "-" + ClientType.MOBILE;
            } else {
                request.getSession().setAttribute(SessionConstant.CLIENT_TYPE, ClientType.PC);
                sessionKey = casUser + "-" + ClientType.PC;
            }
            //踢出同类型客户端的session
            String oldSessionId = redisTemplate.opsForValue().get(sessionKey);
            if (oldSessionId != null && !oldSessionId.equals(request.getSession().getId())) {
                redisOperationsSessionRepository.deleteById(oldSessionId);
                log.info("踢出session：{}", oldSessionId);
            }
            redisTemplate.opsForValue().set(sessionKey, request.getSession().getId());
            log.info("redis缓存设置：{} = {}", sessionKey, request.getSession().getId());

            // 获取用户信息
            ResponseModel<JSONObject> userResponse = uoServerApi.user(casUser);
            log.info("调用uo-server获取用户信息【{}】", userResponse);
            if (!userResponse.isError()) {
                JSONObject userJson = userResponse.getBody();
                String orgFlag = userJson.getStr("orgFlag");
                String loginIp = userJson.getStr("lastLoginIp");
                // 证件照验证状态 2为已验证
                Integer validStatus = userJson.getInt("validStatus");
                // 如果用户所属某个机构，则以该机构作为访问机构
                if (StrUtil.isNotBlank(orgFlag)){
                    // 获取用户的机构信息
                    ResponseModel<JSONObject> orgFlagResponse = uoServerApi.org(null, orgFlag, null, null);
                    log.info("调用uo-server获取【{}】用户的机构信息【{}】", casUser, orgFlagResponse);
                    if (!orgFlagResponse.isError()) {
                        String orgName = orgFlagResponse.getBody().getStr("name");
                        sessionOrg = new JSONObject();
                        sessionOrg.put("flag", orgFlag);
                        sessionOrg.put("name", orgName);
                        request.getSession().setAttribute(SessionConstant.ORG, sessionOrg);
                    }
                }
                // 获取最后用户最后登陆IP的机构信息
                ResponseModel<JSONObject> orgIpResponse = uoServerApi.org(null, null, loginIp, null);
                log.info("调用uo-server获取用户【{}】登陆IP【{}】的机构信息【{}】", casUser, loginIp, orgIpResponse);
                // 用户最后登陆IP未找到对应机构信息，表示校外登陆
                if (orgIpResponse.isError() && orgIpResponse.getStatus() == 404) {
                    request.getSession().setAttribute(SessionConstant.IS_OUT, true);
                    request.getSession().setAttribute(SessionConstant.LEVEL, validStatus == 2 ? 6 : 2);
                } else {
                    request.getSession().setAttribute(SessionConstant.IS_OUT, false);
                    request.getSession().setAttribute(SessionConstant.LEVEL, validStatus == 2 ? 7 : 3);
                }
                request.getSession().setAttribute(SessionConstant.LOGIN_USER, casUser);
            }

        } else {
            // 如果sso退出登陆，清空session中的用户信息
            cleanSession(request);
            JSONObject sessionOrg = (JSONObject) request.getSession().getAttribute(SessionConstant.ORG);
            Boolean isOut = (Boolean) request.getSession().getAttribute(SessionConstant.IS_OUT);
            Integer level = (Integer) request.getSession().getAttribute(SessionConstant.LEVEL);
            if (sessionOrg == null || isOut == null || level ==null){
                String clientIp = HttpUtil.getClientIP(request);
                log.info("客户端访问IP = {}", clientIp);
                ResponseModel<JSONObject> orgResponse = uoServerApi.org(null, null, clientIp, null);
                if (orgResponse.isError()) {
                    //校外访问
                    request.getSession().setAttribute(SessionConstant.IS_OUT, true);
                    request.getSession().setAttribute(SessionConstant.LEVEL, 0);
                } else {
                    //非校外访问
                    request.getSession().setAttribute(SessionConstant.IS_OUT, false);
                    request.getSession().setAttribute(SessionConstant.LEVEL, 1);
                    request.getSession().setAttribute(SessionConstant.ORG, orgResponse.getBody());
                }
            }
        }
        return null;
    }

    private void cleanSession(HttpServletRequest request) {
        if (request.getSession().getAttribute(SessionConstant.LOGIN_USER) != null){
            request.getSession().removeAttribute(SessionConstant.LOGIN_USER);
            request.getSession().removeAttribute(SessionConstant.ORG);
            request.getSession().removeAttribute(SessionConstant.IS_OUT);
            request.getSession().removeAttribute(SessionConstant.LEVEL);
        }
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

}