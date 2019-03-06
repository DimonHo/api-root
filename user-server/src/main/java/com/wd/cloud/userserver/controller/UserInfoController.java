package com.wd.cloud.userserver.controller;

import com.wd.cloud.commons.constant.SessionConstant;
import com.wd.cloud.commons.dto.UserDTO;
import com.wd.cloud.commons.enums.StatusEnum;
import com.wd.cloud.commons.exception.AuthException;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.userserver.entity.UserInfo;
import com.wd.cloud.userserver.model.RegisterModel;
import com.wd.cloud.userserver.service.UserInfoServer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author He Zhigang
 * @date 2019/1/15
 * @Description:
 */
@Slf4j
@Api(value = "上传证件照controller", tags = {"上传证件照接口"})
@RestController
@RequestMapping("/user")
public class UserInfoController {

    @Autowired
    UserInfoServer userInfoServer;

    @Autowired
    HttpServletRequest request;

    @ApiOperation(value = "同步注册")
    @PostMapping("/register")
    public ResponseModel register(@RequestBody RegisterModel registerModel) {
        UserInfo userInfo = null;
        try {
            userInfo = userInfoServer.register(registerModel);
            return ResponseModel.ok().setBody(userInfo);
        } catch (Exception e) {
            return ResponseModel.fail().setBody("用户" + registerModel.getUsername() + "已存在");
        }
    }

    @ApiOperation(value = "上传证件照")
    @PostMapping("/id-photo")
    public ResponseModel getIdPhoto(@NotNull MultipartFile file) {
        if (file == null) {
            return ResponseModel.fail(StatusEnum.DOC_FILE_EMPTY);
        }
        userInfoServer.uploadIdPhoto(file);
        return ResponseModel.ok().setMessage("上传证件照成功!");
    }

    @ApiOperation(value = "审核证件照")
    @PatchMapping("/validated")
    public ResponseModel validateIdPhoto(@RequestParam String username) {
        userInfoServer.validate(username);
        return ResponseModel.ok().setMessage("审核通过。");
    }

    @ApiOperation(value = "获取用户信息")
    @GetMapping("/info")
    public ResponseModel<UserDTO> getUserInfo(@RequestParam String username) {
        UserDTO userDTO = userInfoServer.getUserInfo(username);
        return ResponseModel.ok().setBody(userDTO);
    }

    @ApiOperation(value = "获取当前登录用户信息")
    @GetMapping("/info/logon")
    public ResponseModel<UserDTO> getLoginUser() {
        UserDTO userDTO = (UserDTO) request.getSession().getAttribute(SessionConstant.LOGIN_USER);
        if (userDTO == null) {
            throw new AuthException();
        }
        userDTO = userInfoServer.getUserInfo(userDTO.getUsername());
        return ResponseModel.ok().setBody(userDTO);
    }

    @ApiOperation(value = "用户管理")
    @GetMapping("/getUserInfoSchool")
    public ResponseModel<List<Map<String,Object>>> getUserInfoSchool(@RequestParam(required = false) Map<String, Object> params){
        List<Map<String, Object>> userInfoSchool = userInfoServer.getUserInfoSchool(params);
        return ResponseModel.ok().setBody(userInfoSchool);

    }

    @ApiOperation(value = "获取所有用户信息")
    @GetMapping("/findAll")
    public ResponseModel<List<UserInfo>> findAll(@RequestParam(required = false) String orgName,
                                                 @RequestParam(required = false) String department,
                                                 @RequestParam(required = false) String keyword,
                                                 @PageableDefault(value = 20, sort = {"gmtCreate"}, direction = Sort.Direction.DESC) Pageable pageable){
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("orgName", orgName);
        param.put("department",department);
        param.put("keyword", keyword);
        Page<UserInfo> userInfos = userInfoServer.findAll(pageable,param);
        return ResponseModel.ok().setBody(userInfos);
    }



}
