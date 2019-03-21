package com.wd.cloud.uoserver.aspect;

import com.wd.cloud.commons.constant.SessionConstant;
import com.wd.cloud.commons.dto.UserDTO;
import com.wd.cloud.commons.exception.AuthException;
import com.wd.cloud.uoserver.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author He Zhigang
 * @date 2019/1/28
 * @Description:
 */
@Slf4j
@Aspect
@Component
public class UserInfoAspect {

    @Autowired
    UserService userService;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    RedisOperationsSessionRepository redisOperationsSessionRepository;

    @Autowired
    HttpServletRequest request;

    @Before(value = "@annotation(com.wd.cloud.commons.annotation.ValidateLogin)")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        HttpSession session = request.getSession();
        AttributePrincipal principal = (AttributePrincipal) request.getUserPrincipal();
        if (principal == null) {
            throw new AuthException();
        }
        log.info("用户[{}]已登录", principal.getName());
        UserDTO userDTO = (UserDTO) session.getAttribute(SessionConstant.LOGIN_USER);
        if (userDTO != null && userDTO.getUsername().equals(principal.getName()) ){
            return;
        }
        Map<String, Object> authInfo = principal.getAttributes();
        userDTO = userService.buildUserInfo(authInfo);
        userService.buildSession(userDTO,request,redisTemplate,redisOperationsSessionRepository);

    }


}
