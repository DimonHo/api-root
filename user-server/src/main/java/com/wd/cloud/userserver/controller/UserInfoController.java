package com.wd.cloud.userserver.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.Header;
import cn.hutool.http.useragent.UserAgentUtil;
import com.wd.cloud.commons.constant.SessionConstant;
import com.wd.cloud.commons.dto.UserDTO;
import com.wd.cloud.commons.enums.ClientType;
import com.wd.cloud.commons.enums.StatusEnum;
import com.wd.cloud.commons.exception.AuthException;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.userserver.entity.UserInfo;
import com.wd.cloud.userserver.service.UserInfoServer;
import io.swagger.annotations.Api;
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
import javax.validation.constraints.NotNull;
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

}
