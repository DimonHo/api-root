package com.wd.cloud.authserver.controller;

import cn.hutool.json.JSONUtil;
import com.wd.cloud.authserver.dto.UserInfoDTO;
import com.wd.cloud.authserver.service.AuthService;
import com.wd.cloud.commons.constant.SessionConstant;
import com.wd.cloud.commons.model.ResponseModel;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author He Zhigang
 * @date 2018/6/4
 * @Description:
 */
@Api(value = "用户认证controller", tags = {"用户认证接口"})
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @Autowired
    HttpServletRequest request;

    @GetMapping("/login")
    public ResponseModel<UserInfoDTO> login(@RequestParam String username, @RequestParam String pwd) {
        UserInfoDTO userInfo = authService.loing(username, pwd);
        request.getSession().setAttribute(SessionConstant.LOGIN_USER, JSONUtil.parseObj(userInfo));
        return ResponseModel.ok().setBody(userInfo);
    }

    @GetMapping("/logout")
    public ResponseModel logout() {
        request.getSession().invalidate();
        return ResponseModel.ok();
    }
}
