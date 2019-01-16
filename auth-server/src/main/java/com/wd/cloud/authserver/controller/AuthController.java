package com.wd.cloud.authserver.controller;

import cn.hutool.http.Header;
import cn.hutool.http.useragent.UserAgentUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wd.cloud.authserver.service.AuthService;
import com.wd.cloud.commons.constant.SessionConstant;
import com.wd.cloud.commons.dto.UserDTO;
import com.wd.cloud.commons.enums.ClientType;
import com.wd.cloud.commons.model.ResponseModel;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.session.Session;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author He Zhigang
 * @date 2018/6/4
 * @Description:
 */
@Slf4j
@Api(value = "用户认证controller", tags = {"用户认证接口"})
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @Autowired
    HttpServletRequest request;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    RedisOperationsSessionRepository redisOperationsSessionRepository;

    @PostMapping("/login")
    public ResponseModel<UserDTO> login(@RequestParam String username, @RequestParam String pwd) {
        HttpSession session = request.getSession();
        log.info("sessionId={}", session.getId());
        //验证用户名密码
        UserDTO userInfoDTO = authService.loing(username, pwd);
        // sessionKey
        String sessionKey = null;
        //如果是移动端
        if (UserAgentUtil.parse(request.getHeader(Header.USER_AGENT.name())).getBrowser().isMobile()) {
            session.setAttribute(SessionConstant.CLIENT_TYPE, ClientType.MOBILE);
            sessionKey = username +"-"+ClientType.MOBILE;
        } else {
            session.setAttribute(SessionConstant.CLIENT_TYPE, ClientType.PC);
            sessionKey = username +"-"+ClientType.PC;
        }

        //踢出同类型客户端的session
        String oldSessionId = redisTemplate.opsForValue().get(sessionKey);
        if (oldSessionId != null && !oldSessionId.equals(session.getId())) {
            redisOperationsSessionRepository.deleteById(oldSessionId);
            log.info("踢出session：{}", oldSessionId);
        }
        redisTemplate.opsForValue().set(sessionKey, session.getId());

        log.info("用户[{}]登陆成功", username);
        session.setAttribute(SessionConstant.LOGIN_USER, JSONUtil.parseObj(userInfoDTO));

        return ResponseModel.ok().setBody(userInfoDTO);
    }

    @GetMapping("/logout")
    public ResponseModel logout() {
        log.info("sessionId={}", request.getSession().getId());
        JSONObject oldUser = (JSONObject) request.getSession().getAttribute(SessionConstant.LOGIN_USER);
        if (oldUser != null) {
            request.getSession().invalidate();
            log.info("用户[{}]注销成功", oldUser.getStr("username"));
        }
        return ResponseModel.ok();
    }
}
