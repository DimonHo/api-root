package com.wd.cloud.uoserver.controller;

import com.wd.cloud.commons.annotation.ValidateLogin;
import com.wd.cloud.commons.constant.SessionConstant;
import com.wd.cloud.commons.dto.UserDTO;
import com.wd.cloud.commons.exception.AuthException;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.uoserver.pojo.entity.User;
import com.wd.cloud.uoserver.pojo.vo.PerfectUserVO;
import com.wd.cloud.uoserver.pojo.vo.UserVO;
import com.wd.cloud.uoserver.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    HttpServletRequest request;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    RedisOperationsSessionRepository redisOperationsSessionRepository;

    @Autowired
    UserService userService;

    @ApiOperation(value = "登陆用户的信息",tags = {"用户查询"})
    @GetMapping("/login/info")
    public ResponseModel<UserDTO> getLogin() {
        HttpSession session = request.getSession();
        AttributePrincipal principal = (AttributePrincipal) request.getUserPrincipal();
        if (principal == null) {
            return null;
        }
        log.info("用户[{}]已登录", principal.getName());
        UserDTO userDTO = (UserDTO) session.getAttribute(SessionConstant.LOGIN_USER);
        if (userDTO == null || !userDTO.getUsername().equals(principal.getName()) ){
            Map<String, Object> authInfo = principal.getAttributes();
            userDTO = userService.buildUserInfo(authInfo);
            userService.buildSession(userDTO,request,redisTemplate,redisOperationsSessionRepository);
        }
        return ResponseModel.ok().setBody(userDTO);
    }

    @ApiOperation(value = "新增用户",tags = {"用户注册"})
    @PostMapping("/user")
    public ResponseModel registerUser(@RequestBody UserVO userVO) {
        try {
            log.info("注册新用户：[{}]",userVO.toString());
            User user = userService.registerUser(userVO);
            return ResponseModel.ok().setBody(user);
        } catch (Exception e) {
            return ResponseModel.fail().setBody("用户" + userVO.getUsername() + "已存在");
        }
    }

    @ApiOperation(value = "完善用户信息",tags = {"用户修改资料"})
    @PutMapping("/user")
    public ResponseModel updateUser(@RequestBody PerfectUserVO perfectUserVO) {
        AttributePrincipal principal = (AttributePrincipal) request.getUserPrincipal();
        if (principal == null) {
            throw new AuthException();
        }
        return ResponseModel.ok().setBody(userService.perfectUser(perfectUserVO));
    }

    @ApiOperation(value = "获取用户信息",tags = {"用户查询"})
    @GetMapping("/user")
    public ResponseModel<UserDTO> user(@RequestParam String id) {
        UserDTO userDTO = userService.getUserDTO(id);
        return ResponseModel.ok().setBody(userDTO);
    }


    /**
     * 上传头像
     *
     * @param file
     * @return
     */
    @ApiOperation(value = "上传头像",tags = {"用户修改资料"})
    @ValidateLogin
    @PostMapping("/user/head-img")
    public ResponseModel uploadHeadImg(MultipartFile file) {
        UserDTO userDTO = (UserDTO) request.getSession().getAttribute(SessionConstant.LOGIN_USER);
        if (userDTO == null) {
            throw new AuthException();
        }
        return ResponseModel.ok().setBody(userService.uploadHeadImg(userDTO.getUsername(), file));
    }

    /**
     * 上传证件照
     *
     * @param file
     * @return
     */
    @ApiOperation(value = "上传证件照",tags = {"用户修改资料"})
    @ValidateLogin
    @PostMapping("/user/id-photo")
    public ResponseModel uploadIdPhoto(MultipartFile file) {
        UserDTO userDTO = (UserDTO) request.getSession().getAttribute(SessionConstant.LOGIN_USER);
        if (userDTO == null) {
            throw new AuthException();
        }
        return ResponseModel.ok().setBody(userService.uploadIdPhoto(userDTO.getUsername(), file));
    }
}
