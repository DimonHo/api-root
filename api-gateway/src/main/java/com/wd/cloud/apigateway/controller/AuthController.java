package com.wd.cloud.apigateway.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.useragent.UserAgentUtil;
import com.wd.cloud.apigateway.config.CasProperties;
import com.wd.cloud.apigateway.utils.CasUtil;
import com.wd.cloud.apigateway.utils.HttpUtil;
import com.wd.cloud.commons.constant.SessionConstant;
import com.wd.cloud.commons.dto.UserDTO;
import com.wd.cloud.commons.enums.ClientType;
import com.wd.cloud.commons.exception.AuthException;
import com.wd.cloud.commons.model.ResponseModel;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author He Zhigang
 * @date 2019/1/19
 * @Description:
 */
@Slf4j
@Api(value = "用户认证controller", tags = {"用户认证接口"})
@RestController
public class AuthController {

    @Autowired
    HttpServletRequest request;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    RedisOperationsSessionRepository redisOperationsSessionRepository;

    @Autowired
    HttpServletResponse response;

    @Autowired
    CasProperties casProperties;

    @PostMapping("/login")
    public ResponseModel<UserDTO> login(@RequestParam String username,
                                        @RequestParam String password,
                                        @RequestParam(required = false) boolean rememberMe) {
        //Hutool使用jdk的cookie自动管理，你第二次请求附带了第一次的cookie，所以服务端不再写cookie.(作者原话)
        //需要调用closeCookie()方法关闭cookie,否则HttpResponse.header("Set-Cookie")无法获取值
        HttpRequest.closeCookie();
        //获取登陆页面
        HttpResponse loginViewResponse = HttpRequest.get(casProperties.getServerLoginUrl()).setFollowRedirects(false).execute();
        Document loginViewDoc = Jsoup.parse(loginViewResponse.body());
        String setCookie = loginViewResponse.header("Set-Cookie");
        String execution = loginViewDoc.getElementsByAttributeValue("name", "execution").attr("value");
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("execution", execution);
        params.put("_eventId", "submit");
        params.put("submit", "LOGIN");
        params.put("service", request.getRequestURL().toString().replace("/login", "/userinfo"));
        if (rememberMe) {
            params.put("rememberMe", true);
        }
        // 执行登陆请求
        HttpResponse loginResultResponse = HttpRequest.post(casProperties.getServerLoginUrl())
                .header("User-Agent", request.getHeader("User-Agent"))
                .header("Cookie", setCookie)
                .setFollowRedirects(false)
                .form(params)
                .execute();
        // 获取tgc
        String tgcCookie = loginResultResponse.header("Set-Cookie");

        // 种植cookie
        List<String> tgcCookieParams = StrUtil.splitTrim(tgcCookie, ";");
        Cookie cookie = new Cookie("TGC", tgcCookieParams.get(0).split("=")[1]);
        for (String param : tgcCookieParams) {
            List<String> tgcCookieParam = StrUtil.splitTrim(param, "=");
            if ("Path".equals(tgcCookieParam.get(0))) {
                cookie.setPath(tgcCookieParam.get(1));
            }
            if ("Domain".equals(tgcCookieParam.get(0))) {
                cookie.setDomain(tgcCookieParam.get(1));
            }
            if ("Max-Age".equals(tgcCookieParam.get(0))) {
                cookie.setMaxAge(Integer.parseInt(tgcCookieParam.get(1)));
            }
            if ("HttpOnly".equals(tgcCookieParam.get(0))) {
                cookie.setHttpOnly(true);
            }
        }
        response.addCookie(cookie);
        Document loginResultDoc = Jsoup.parse(loginResultResponse.body());
        if (loginResultResponse.getStatus() == 302) {
            String st = CasUtil.getSt(request,
                    casProperties.getServerLoginUrl(),
                    request.getRequestURL().toString().replace("/login", "/userinfo"),
                    tgcCookie);
            return ResponseModel.ok().setBody(request.getRequestURL().toString().replace("/login", "/userinfo?ticket=" + st));
        } else {
            throw new AuthException();
        }
    }

    @GetMapping("/userinfo")
    public ResponseModel<UserDTO> userInfo(@RequestParam(required = false) String ticket) {
        HttpSession session = request.getSession();
        log.info("sessionId={}", session.getId());
        AttributePrincipal principal = (AttributePrincipal) request.getUserPrincipal();
        if (principal == null) {
            throw new AuthException();
        }
        Map<String, Object> authInfo = principal.getAttributes();
        UserDTO userDTO = BeanUtil.mapToBean(authInfo, UserDTO.class, true);
        // session Key
        String sessionKey = null;
        //如果是移动端
        if (UserAgentUtil.parse(request.getHeader(Header.USER_AGENT.name())).getBrowser().isMobile()) {
            session.setAttribute(SessionConstant.CLIENT_TYPE, ClientType.MOBILE);
            sessionKey = userDTO.getUsername() + "-" + ClientType.MOBILE;
        } else {
            session.setAttribute(SessionConstant.CLIENT_TYPE, ClientType.PC);
            sessionKey = userDTO.getUsername() + "-" + ClientType.PC;
        }

        //踢出同类型客户端的session
        String oldSessionId = redisTemplate.opsForValue().get(sessionKey);
        if (oldSessionId != null && !oldSessionId.equals(session.getId())) {
            redisOperationsSessionRepository.deleteById(oldSessionId);
            log.info("踢出session：{}", oldSessionId);
        }
        redisTemplate.opsForValue().set(sessionKey, session.getId());

        log.info("用户[{}]登陆成功", userDTO.getUsername());
        // 如果用户有所属机构，则把有效机构设置为用户所属机构
        if (userDTO.getOrg() != null) {
            request.getSession().setAttribute(SessionConstant.ORG, userDTO.getOrg());
        }
        session.setAttribute(SessionConstant.LOGIN_USER, userDTO);
        // 登陆成功 level +2
        Integer level = session.getAttribute(SessionConstant.LEVEL) == null ? 0 : (Integer) session.getAttribute(SessionConstant.LEVEL);

        if (level < 2) {
            level += 2;
        }
        // 如果是已认证用户，level + 4
        if (level < 4 && userDTO.isValidated()) {
            level += 4;
        }
        session.setAttribute(SessionConstant.LEVEL, level);
        return ResponseModel.ok().setBody(userDTO);
    }


    @GetMapping("/logout")
    public ResponseModel logout() {
        UserDTO userDTO = (UserDTO) request.getSession().getAttribute(SessionConstant.LOGIN_USER);
        String cookieStr = HttpUtil.getCookieStr(request);
        // sso认证中心登出
        HttpResponse logoutResp = HttpRequest.get(casProperties.getServerUrlPrefix() + "/logout")
                .header("User-Agent", request.getHeader("User-Agent"))
                .cookie(cookieStr)
                .execute();
        if (logoutResp.getStatus() == 200) {
            request.getSession().invalidate();
            return ResponseModel.ok().setMessage("用户：[" + userDTO.getUsername() + "]注销成功");
        } else {
            return ResponseModel.fail().setMessage("用户：[" + userDTO.getUsername() + "]注销失败");
        }
    }


}
