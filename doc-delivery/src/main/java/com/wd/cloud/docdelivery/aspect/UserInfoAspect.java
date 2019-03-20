package com.wd.cloud.docdelivery.aspect;

import cn.hutool.core.util.StrUtil;
import com.wd.cloud.commons.constant.SessionConstant;
import com.wd.cloud.commons.dto.UserDTO;
import com.wd.cloud.commons.exception.AuthException;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.docdelivery.feign.UoServerApi;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Objects;

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
    UoServerApi uoServerApi;

    @Before(value = "@annotation(com.wd.cloud.commons.annotation.ValidateUser)")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();
        HttpSession session = request.getSession();
        UserDTO userDTO = (UserDTO) session.getAttribute(SessionConstant.LOGIN_USER);
        String username = request.getParameter("username");
        if (StrUtil.isNotBlank(username)) {
            if (userDTO == null) {
                // 获取用户信息
                ResponseModel<UserDTO> userDTOResponse = uoServerApi.user(username);
                if (!userDTOResponse.isError()) {
                    userDTO = userDTOResponse.getBody();
                    session.setAttribute(SessionConstant.LOGIN_USER, userDTO);
                }
            } else {
                // 如果主动传的用户名与session中的用户不匹配，则视为非法请求
                if (!userDTO.getUsername().equals(username)) {
                    throw new AuthException("非法的请求！");
                }
            }
        }
    }
}
