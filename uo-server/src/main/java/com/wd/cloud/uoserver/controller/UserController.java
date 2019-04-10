package com.wd.cloud.uoserver.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wd.cloud.commons.annotation.ValidateLogin;
import com.wd.cloud.commons.constant.SessionConstant;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.commons.util.StrUtil;
import com.wd.cloud.uoserver.pojo.dto.UserDTO;
import com.wd.cloud.uoserver.pojo.entity.User;
import com.wd.cloud.uoserver.pojo.vo.PerfectUserVO;
import com.wd.cloud.uoserver.pojo.vo.UserVO;
import com.wd.cloud.uoserver.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

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
    UserService userService;

    @ApiOperation(value = "登陆用户的信息", tags = {"用户查询"})
    @ValidateLogin
    @GetMapping("/login/info")
    public ResponseModel getLogin() {
        JSONObject loginUser = JSONUtil.parseObj(request.getSession().getAttribute(SessionConstant.LOGIN_USER));
        String username = loginUser != null ? loginUser.getStr("username") : null;
        if (StrUtil.isBlank(username)) {
            return ResponseModel.fail();
        }
        return ResponseModel.ok().setBody(userService.getUserDTO(username));
    }

    @ApiOperation(value = "新增用户", tags = {"用户注册"})
    @PostMapping("/user")
    public ResponseModel registerUser(@RequestBody UserVO userVO) {
        try {
            log.info("注册新用户：[{}]", userVO.toString());
            User user = userService.registerUser(userVO);
            return ResponseModel.ok().setBody(user);
        } catch (Exception e) {
            return ResponseModel.fail().setBody("用户" + userVO.getUsername() + "已存在");
        }
    }

    @ApiOperation(value = "完善用户信息", tags = {"用户修改资料"})
    @ValidateLogin
    @PutMapping("/user")
    public ResponseModel updateUser(@RequestBody PerfectUserVO perfectUserVO) {
        return ResponseModel.ok().setBody(userService.perfectUser(perfectUserVO));
    }

    @ApiOperation(value = "获取用户信息", tags = {"用户查询"})
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
    @ApiOperation(value = "上传头像", tags = {"用户修改资料"})
    @ValidateLogin
    @PostMapping("/user/head-img")
    public ResponseModel uploadHeadImg(MultipartFile file) {
        JSONObject loginUser = (JSONObject) request.getSession().getAttribute(SessionConstant.LOGIN_USER);
        String username = loginUser != null ? loginUser.getStr("username") : null;
        return ResponseModel.ok().setBody(userService.uploadHeadImg(username, file));
    }

    /**
     * 检查用户名是否存在
     *
     * @param username
     * @return
     */
    @ApiOperation(value = "检查用户名", tags = {"用户查询"})
    @GetMapping("/user/check/username")
    public ResponseModel checkUsername(@RequestParam String username) {
        boolean isExists = userService.checkUsernameExists(username);
        return ResponseModel.ok().setMessage(isExists ? "用户名已存在" : "用户名不存在").setBody(isExists);
    }

    /**
     * 检查邮箱是否存在
     *
     * @param email
     * @return
     */
    @ApiOperation(value = "检查邮箱", tags = {"用户查询"})
    @GetMapping("/user/check/email")
    public ResponseModel checkEmail(@RequestParam String email) {
        boolean isExists = userService.checkEmailExists(email);
        return ResponseModel.ok().setMessage(isExists ? "邮箱已存在" : "邮箱不存在").setBody(isExists);
    }

    /**
     * 上传证件照
     *
     * @param file
     * @return
     */
    @ApiOperation(value = "上传证件照", tags = {"用户修改资料"})
    @ValidateLogin
    @PostMapping("/user/id-photo")
    public ResponseModel uploadIdPhoto(MultipartFile file) {
        JSONObject loginUser = (JSONObject) request.getSession().getAttribute(SessionConstant.LOGIN_USER);
        String username = loginUser != null ? loginUser.getStr("username") : null;
        return ResponseModel.ok().setBody(userService.uploadIdPhoto(username, file));
    }
}
