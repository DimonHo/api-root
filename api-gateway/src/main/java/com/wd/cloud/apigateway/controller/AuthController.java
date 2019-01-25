package com.wd.cloud.apigateway.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.Header;
import cn.hutool.http.useragent.UserAgentUtil;
import com.wd.cloud.apigateway.service.AuthService;
import com.wd.cloud.commons.constant.SessionConstant;
import com.wd.cloud.commons.dto.UserDTO;
import com.wd.cloud.commons.enums.ClientType;
import com.wd.cloud.commons.model.ResponseModel;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
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
    AuthService authService;

    @Autowired
    HttpServletRequest request;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    RedisOperationsSessionRepository redisOperationsSessionRepository;

    @GetMapping("/loger/info")
    public ResponseModel<UserDTO> login(@RequestParam String redirectUrl){
        HttpSession session = request.getSession();
        log.info("sessionId={}", session.getId());
        AttributePrincipal principal = (AttributePrincipal) request.getUserPrincipal();
        Map<String,Object> authInfo = principal.getAttributes();
        UserDTO userDTO = BeanUtil.mapToBean(authInfo,UserDTO.class,true);
        // sessionKey
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
        Integer level = (Integer) session.getAttribute(SessionConstant.LEVEL);
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
        log.info("sessionId={}", request.getSession().getId());
        UserDTO userDTO = (UserDTO) request.getSession().getAttribute(SessionConstant.LOGIN_USER);
        if (userDTO != null) {
            request.getSession().invalidate();
            log.info("用户[{}]注销成功", userDTO.getUsername());
        }
        return ResponseModel.ok();
    }
}
