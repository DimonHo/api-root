package com.wd.cloud.uoserver.controller;

import cn.hutool.http.Header;
import cn.hutool.http.useragent.UserAgentUtil;
import com.wd.cloud.commons.constant.SessionConstant;
import com.wd.cloud.commons.dto.UserDTO;
import com.wd.cloud.commons.enums.ClientType;
import com.wd.cloud.commons.exception.AuthException;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.uoserver.entity.User;
import com.wd.cloud.uoserver.model.RegisterModel;
import com.wd.cloud.uoserver.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
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

    @ApiOperation(value = "新增用户")
    @PostMapping("/user/info")
    public ResponseModel register(@RequestBody RegisterModel registerModel) {
        try {
            User user = userService.register(registerModel);
            return ResponseModel.ok().setBody(user);
        } catch (Exception e) {
            return ResponseModel.fail().setBody("用户" + registerModel.getUsername() + "已存在");
        }
    }

    @ApiOperation(value = "获取用户信息")
    @GetMapping("/user/info")
    public ResponseModel<UserDTO> getUserInfo(@RequestParam String username) {
        UserDTO userDTO = userService.findByUsername(username);
        return ResponseModel.ok().setBody(userDTO);
    }

    @ApiOperation(value = "登陆用户的信息")
    @GetMapping("/login/info")
    public ResponseModel<UserDTO> getLogin() {
        AttributePrincipal principal = (AttributePrincipal) request.getUserPrincipal();
        if (principal == null) {
            throw new AuthException();
        }
        log.info("用户[{}]登陆成功", principal.getName());
        Map<String, Object> authInfo = principal.getAttributes();
        UserDTO userDTO = userService.buildUserInfo(authInfo);
        createSession(userDTO);
        return ResponseModel.ok().setBody(userDTO);
    }

    /**
     * 上传头像
     *
     * @param file
     * @return
     */
    @ApiOperation(value = "上传头像")
    @PostMapping("/user/photo")
    public ResponseModel uploadPhoto(MultipartFile file) {
        UserDTO userDTO = (UserDTO) request.getSession().getAttribute(SessionConstant.LOGIN_USER);
        if (userDTO == null) {
            throw new AuthException();
        }
        userService.uploadIdPhoto(userDTO.getUsername(), file);
        return ResponseModel.ok();
    }

    /**
     * 上传证件照
     *
     * @param file
     * @return
     */
    @ApiOperation(value = "上传证件照")
    @PostMapping("/user/id-photo")
    public ResponseModel uploadIdPhoto(MultipartFile file) {
        UserDTO userDTO = (UserDTO) request.getSession().getAttribute(SessionConstant.LOGIN_USER);
        if (userDTO == null) {
            throw new AuthException();
        }
        userService.uploadIdPhoto(userDTO.getUsername(), file);
        return ResponseModel.ok();
    }

    @ApiOperation(value = "审核证件照")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "username", paramType = "String", type = "query"),
            @ApiImplicitParam(name = "pass", value = "审核通过/不通过", paramType = "Boolean", type = "query"),
    })
    @PatchMapping("/user/id-photo")
    public ResponseModel validateIdPhoto(@RequestParam String username, @RequestParam Boolean pass) {
        userService.auditIdPhoto(username, pass);
        return ResponseModel.ok().setMessage("审核通过。");
    }


    private void createSession(UserDTO userDTO) {
        // session Key
        String sessionKey = null;
        //如果是移动端
        if (UserAgentUtil.parse(request.getHeader(Header.USER_AGENT.name())).getBrowser().isMobile()) {
            request.getSession().setAttribute(SessionConstant.CLIENT_TYPE, ClientType.MOBILE);
            sessionKey = userDTO.getUsername() + "-" + ClientType.MOBILE;
        } else {
            request.getSession().setAttribute(SessionConstant.CLIENT_TYPE, ClientType.PC);
            sessionKey = userDTO.getUsername() + "-" + ClientType.PC;
        }

        //踢出同类型客户端的session
        String oldSessionId = redisTemplate.opsForValue().get(sessionKey);
        if (oldSessionId != null && !oldSessionId.equals(request.getSession().getId())) {
            redisOperationsSessionRepository.deleteById(oldSessionId);
            log.info("踢出session：{}", oldSessionId);
        }
        redisTemplate.opsForValue().set(sessionKey, request.getSession().getId());
        log.info("redis缓存设置：{} = {}", sessionKey, request.getSession().getId());
        // 如果用户有所属机构，则把有效机构设置为用户所属机构
        if (userDTO.getOrg() != null) {
            request.getSession().setAttribute(SessionConstant.ORG, userDTO.getOrg());
            log.info("设置ORG session:{}", userDTO.getOrg().toString());
        }
        request.getSession().setAttribute(SessionConstant.LOGIN_USER, userDTO);
        log.info("设置LOGIN_USER session:{}", userDTO.toString());
        // 登陆成功 level +2
        Integer level = request.getSession().getAttribute(SessionConstant.LEVEL) == null ? 0 : (Integer) request.getSession().getAttribute(SessionConstant.LEVEL);

        if (level < 2) {
            level += 2;
        }
        // 如果是已认证用户，level + 4
        if (level < 4 && userDTO.isValidated()) {
            level += 4;
        }
        request.getSession().setAttribute(SessionConstant.LEVEL, level);
        log.info("设置LEVEL session:{}", level);
    }
}
