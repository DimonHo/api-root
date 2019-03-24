package com.wd.cloud.uoserver.controller;

import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.uoserver.pojo.dto.UserDTO;
import com.wd.cloud.uoserver.pojo.vo.BackUserVO;
import com.wd.cloud.uoserver.pojo.vo.PermissionVO;
import com.wd.cloud.uoserver.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: He Zhigang
 * @Date: 2019/3/18 18:43
 * @Description: 用户和机构管理接口
 */
@Slf4j
@RestController
public class UserManagerController {

    @Autowired
    UserService userService;

    @ApiOperation(value = "后台新增用户", tags = {"用户管理"})
    @PostMapping("/manager/user")
    public ResponseModel createUser(@RequestBody BackUserVO backUserVO){
        return ResponseModel.ok().setBody(userService.addUser(backUserVO));
    }

    @ApiOperation(value = "后台更新用户", tags = {"用户管理"})
    @PutMapping("/manager/user")
    public ResponseModel modifyUser(@RequestBody BackUserVO backUserVO){
        return ResponseModel.ok().setBody(userService.saveUser(backUserVO));
    }

    @ApiOperation(value = "查询用户列表", tags = {"用户管理","用户查询"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgFlag", value = "机构标识", paramType = "String", type = "query"),
            @ApiImplicitParam(name = "orgName", value = "机构全称", paramType = "String", type = "query"),
            @ApiImplicitParam(name = "deptId", value = "部门ID", paramType = "Long", type = "query"),
            @ApiImplicitParam(name = "deptName", value = "部门全称，必须和orgFlag 或 orgName联合使用", paramType = "String", type = "query"),
            @ApiImplicitParam(name = "userType", value = "用户类型", paramType = "Integer", type = "query"),
            @ApiImplicitParam(name = "valid", value = "是否只返回上传过证件照的记录", paramType = "Boolean", type = "query"),
            @ApiImplicitParam(name = "validStatus", value = "认证状态", paramType = "Integer", type = "query"),
            @ApiImplicitParam(name = "keyword", value = "用户名或邮箱模糊查询", paramType = "String", type = "query")
    })
    @GetMapping("/manager/user")
    public ResponseModel getUsers(
            @RequestParam(required = false) String orgFlag,
            @RequestParam(required = false) String orgName,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) String deptName,
            @RequestParam(required = false) List<Integer> userType,
            @RequestParam(required = false) Boolean valid,
            @RequestParam(required = false) List<Integer> validStatus,
            @RequestParam(required = false) String keyword,
            @PageableDefault(value = 20, sort = {"gmtCreate"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<UserDTO> userDTOPage = userService.queryUsers(orgFlag, orgName, deptId, deptName, userType, valid, validStatus, keyword, pageable);
        return ResponseModel.ok().setBody(userDTOPage);
    }

    @ApiOperation(value = "删除用户",tags = {"用户管理"})
    @DeleteMapping("/manager/user")
    public ResponseModel deleteUser(@RequestParam String username) {
        userService.deleteUser(username);
        return ResponseModel.ok().setMessage("用户["+username+"]已经被删除");
    }

    @ApiOperation(value = "审核证件照",tags = {"用户管理"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "被审核用户名", paramType = "String", type = "query"),
            @ApiImplicitParam(name = "pass", value = "审核通过/不通过", paramType = "Boolean", type = "query"),
            @ApiImplicitParam(name = "handlerName", value = "审核人", paramType = "String", type = "query"),
            @ApiImplicitParam(name = "remark", value = "审核失败信息", paramType = "String", type = "query")
    })
    @PutMapping("/manager/user/id-photo")
    public ResponseModel validateIdPhoto(@RequestParam String username,
                                         @RequestParam(defaultValue = "false") Boolean pass,
                                         @RequestParam String handlerName,
                                         @RequestParam(required = false) String remark) {
        userService.auditIdPhoto(username, pass, handlerName, remark);
        return ResponseModel.ok().setMessage("审核成功");
    }

    @ApiOperation(value = "修改用户权限",tags = {"用户管理"})
    @ApiImplicitParam(name = "handlerName", value = "操作人", paramType = "String", type = "query")
    @PostMapping("/manager/user/permission")
    public ResponseModel updateUserPermission(@RequestBody PermissionVO permissionVo,
                                              @RequestParam(required = false) String handlerName){
        userService.savePermission(permissionVo,handlerName);
        return ResponseModel.ok();
    }

}
