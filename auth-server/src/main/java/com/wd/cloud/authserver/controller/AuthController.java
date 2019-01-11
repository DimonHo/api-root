package com.wd.cloud.authserver.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wd.cloud.authserver.dto.UserInfoDTO;
import com.wd.cloud.authserver.service.AuthService;
import com.wd.cloud.commons.constant.SessionConstant;
import com.wd.cloud.commons.model.ResponseModel;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/login")
    public ResponseModel<UserInfoDTO> login(@RequestParam String username, @RequestParam String pwd) {
        UserInfoDTO userInfoDTO = authService.loing(username, pwd);
        if (request.getSession().getAttribute(SessionConstant.LOGIN_USER) != null) {
            request.getSession().removeAttribute(SessionConstant.LOGIN_USER);
        }
        request.getSession().setAttribute(SessionConstant.LOGIN_USER, JSONUtil.parseObj(userInfoDTO));
        return ResponseModel.ok().setBody(userInfoDTO);
    }

    @GetMapping("/logout")
    public ResponseModel logout() {
        JSONObject userInfo = (JSONObject) request.getSession().getAttribute(SessionConstant.LOGIN_USER);
        if (userInfo != null) {
            request.getSession().removeAttribute(SessionConstant.LOGIN_USER);
        }
        return ResponseModel.ok();
    }
}
