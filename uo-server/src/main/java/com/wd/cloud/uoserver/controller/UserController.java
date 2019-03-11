package com.wd.cloud.uoserver.controller;

import cn.hutool.http.Header;
import cn.hutool.http.useragent.UserAgentUtil;
import com.wd.cloud.commons.constant.SessionConstant;
import com.wd.cloud.commons.dto.UserDTO;
import com.wd.cloud.commons.enums.ClientType;
import com.wd.cloud.commons.exception.AuthException;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.uoserver.entity.AuditUserInfo;
import com.wd.cloud.uoserver.entity.User;
import com.wd.cloud.uoserver.model.RegisterModel;
import com.wd.cloud.uoserver.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.web.PageableDefault;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
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
    
    
    @ApiOperation(value = "记录审核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userName", value = "用户名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "idPhoto",value = "证件照" , dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "nickName",value = "真实姓名" , dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "orgName",value = "学校" , dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "department", value = "院系", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "identity", value = "身份(1-学生,2-老师,3-其它)", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "departmentId", value = "职工号/学号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "education", value = "教育程度(1-博士,2-硕士,3-本科,4-大专,5-其它)", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "sex", value = "性别(1-男,2-女)", dataType = "Short", paramType = "query"),
            @ApiImplicitParam(name = "entranceTime", value = "入学年份", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "email", value = "邮箱", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "permission", value = "访问权限", dataType = "Integer", paramType = "query")
    })
    @PostMapping("/give")
    public ResponseModel<AuditUserInfo>  give(@RequestParam(required = false) String userName,
                                              @RequestParam(required = false) String idPhoto,
                                              @RequestParam(required = false) String nickName,
                                              @RequestParam(required = false) String orgName,
                                              @RequestParam(required = false) String department,
                                              @RequestParam(required = false) Integer identity,
                                              @RequestParam(required = false) String departmentId,
                                              @RequestParam(required = false) Integer education,
                                              @RequestParam(required = false) Short sex,
                                              @RequestParam(required = false) String entranceTime,
                                              @RequestParam(required = false) String email,
                                              @RequestParam(required = false) Integer permission){
        userService.give(userName,idPhoto,nickName,orgName,department,identity,departmentId,education,sex,entranceTime,email,permission);
        return ResponseModel.ok().setMessage("已成功记录一条信息");

    }

    @ApiOperation(value = "根据用户名获取详细信息")
    @ApiImplicitParam(name = "userName", value = "用户名", dataType = "String", paramType = "query")
    @GetMapping("/getUserName")
    public ResponseModel<AuditUserInfo> getUserName(@RequestParam(required = false) String userName){
        AuditUserInfo AuditUserInfo = userService.getUserName(userName);
        return ResponseModel.ok().setBody(AuditUserInfo);
    }

    @ApiOperation(value = "获取所有校外访问申请审核信息")
    @GetMapping("/findAll")
    public ResponseModel<List<AuditUserInfo>> findAll(@RequestParam(required = false) Integer status,
                                                      @RequestParam(required = false) String keyword,
                                                      @PageableDefault(value = 20, sort = {"gmtCreate"}, direction = Sort.Direction.DESC) Pageable pageable){
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("status", status);
        param.put("keyword", keyword);
        Page<AuditUserInfo> AuditUserInfos = userService.findAll(pageable,param);
        return ResponseModel.ok().setBody(AuditUserInfos);
    }

    @ApiOperation(value = "根据ID获取详细信息")
    @GetMapping("/findById")
    public ResponseModel<AuditUserInfo> findById(Long id){
        AuditUserInfo AuditUserInfo = userService.findById(id);
        return ResponseModel.ok().setBody(AuditUserInfo);
    }

    @ApiOperation(value = "审核通过")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "permission", value = "访问权限", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "handlerName", value = "操作人", dataType = "Integer", paramType = "query")
    })
    @PostMapping("/apply")
    public ResponseModel<AuditUserInfo> apply(@RequestParam(required = false)Long id,
                                            @RequestParam(required = false)Integer permission,
                                            @RequestParam(required = false)String handlerName){
        userService.apply(id, permission, handlerName);
        return ResponseModel.ok().setMessage("审核已通过");
    }

    @ApiOperation(value = "审核未通过")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "permission", value = "访问权限", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "handlerName", value = "操作人", dataType = "Integer", paramType = "query")
    })
    @PostMapping("/notApply")
    public ResponseModel<AuditUserInfo> notApply(@RequestParam(required = false)Long id,
                                               @RequestParam(required = false)Integer permission,
                                               @RequestParam(required = false)String handlerName){
        userService.notApply(id, permission, handlerName);
        return ResponseModel.ok().setMessage("审核已通过");
    }

    @ApiOperation(value = "获取所有用户信息")
    @GetMapping("/findUserAll")
    public ResponseModel<List<User>> findUserAll(@RequestParam(required = false) String orgName,
                                                 @RequestParam(required = false) String department,
                                                 @RequestParam(required = false) String keyword,
                                                 @PageableDefault(value = 20, sort = {"gmtCreate"}, direction = Sort.Direction.DESC) Pageable pageable){
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("orgName", orgName);
        param.put("department",department);
        param.put("keyword", keyword);
        Page<User> userInfos = userService.findUserAll(pageable,param);
        return ResponseModel.ok().setBody(userInfos);
    }


    @ApiOperation(value = "用户管理")
    @GetMapping("/getUserInfoSchool")
    public ResponseModel<List<Map<String,Object>>> getUserInfoSchool(@RequestParam(required = false) Map<String, Object> params){
        List<Map<String, Object>> userInfoSchool = userService.getUserInfoSchool(params);
        return ResponseModel.ok().setBody(userInfoSchool);

    }


    @ApiOperation("根据ID修改用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pwd", value = "密码", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "nickName", value = "真实姓名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "orgName", value = "学校", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "department", value = "院系", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "identity", value = "身份", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "entranceTime", value = "入学年份", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "departmentId", value = "职工号/学号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "education", value = "教育程度", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "userType", value = "用户类型", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "sex", value = "性别", dataType = "Short", paramType = "query"),
            @ApiImplicitParam(name = "permission", value = "是否校外访问", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "orgId", value = "学校ID", dataType = "Long", paramType = "query"),
    })
    @PostMapping("/updateUser")
    public ResponseModel<User> updateUser(@RequestParam(required = false)Long id,
                                          @RequestParam(required = false)String pwd,
                                          @RequestParam(required = false)String nickName,
                                          @RequestParam(required = false)String orgName,
                                          @RequestParam(required = false)String department,
                                          @RequestParam(required = false)Integer identity,
                                          @RequestParam(required = false)String entranceTime,
                                          @RequestParam(required = false)String departmentId,
                                          @RequestParam(required = false)Integer education,
                                          @RequestParam(required = false)Integer userType,
                                          @RequestParam(required = false)Short sex,
                                          @RequestParam(required = false)Integer permission,
                                          @RequestParam(required = false)Long orgId){
        userService.updateUser(id, pwd, nickName, orgName, department, identity,entranceTime, departmentId, education, userType, sex, permission,orgId);
        return ResponseModel.ok().setMessage("记录修改成功");
    }


    @ApiOperation(value = "根据userId查询数据")
    @GetMapping("/findByUserId")
    public ResponseModel<User> findByUserId(@RequestParam(required = false)Long id){
        User user = userService.findByUserId(id);
        return ResponseModel.ok().setBody(user);
    }

    @ApiOperation(value = "是否禁用账号")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "forbidden", value = "是否禁用（1、否-----2、是）", dataType = "Integer", paramType = "query"),
    })
    @PostMapping("/findByUserType")
    public ResponseModel<User> findByUserType(@RequestParam(required = false)Long id ,
                                        @RequestParam(required = false)Integer forbidden){
        userService.findByUserType(id, forbidden);
        return ResponseModel.ok().setMessage("修改成功");
    }

    @ApiOperation(value = "删除")
    @PostMapping("/deleteUserId")
    public ResponseModel deleteUserId(@RequestParam(required = false)Long id){
        userService.deleteUserId(id);
        return ResponseModel.ok().setMessage("修改成功");
    }

    @ApiOperation(value = "新增用户")
    @GetMapping("/userSave")
    public  ResponseModel<User> userSave(@Valid User user) {
        return ResponseModel.ok().setBody(userService.userSave(user));
    }

    @ApiOperation(value = "判断邮箱是否存在")
    @GetMapping("/findByEmail")
    public  ResponseModel<User> findByEmail(@RequestParam(required = false)String email) {
        User byEmail = userService.findByEmail(email);
        return ResponseModel.ok().setBody(byEmail);
    }

    @ApiOperation(value = "根据学校名称获取统计学校院系")
    @GetMapping("/findByCountOrgName")
    public  ResponseModel<List<Map<String,Object>>> findByCountOrgName(@RequestParam(required = false)String orgName) {
        List<Map<String, Object>> byCountOrgName = userService.findByCountOrgName(orgName);
        return ResponseModel.ok().setBody(byCountOrgName);
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
