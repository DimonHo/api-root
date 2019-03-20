package com.wd.cloud.uoserver.controller;

import com.wd.cloud.commons.dto.UserDTO;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.uoserver.pojo.entity.VUser;
import com.wd.cloud.uoserver.pojo.vo.BackUserVO;
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

    @PostMapping("/manager/user")
    public ResponseModel<UserDTO> createUser(@RequestBody BackUserVO backUserVO){
        return ResponseModel.ok();
    }

    @PutMapping("/manager/user")
    public ResponseModel<UserDTO> modifyUser(@RequestBody BackUserVO backUserVO){
        return ResponseModel.ok();
    }

    @ApiOperation(value = "查询用户列表")
    @GetMapping("/manager/user")
    public ResponseModel<Page<VUser>> getUsers(
            @RequestParam(required = false) String orgFlag,
            @RequestParam(required = false) String orgName,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Integer userType,
            @RequestParam(required = false) String keyword,
            @PageableDefault(value = 20, sort = {"gmtCreate"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<VUser> userDTOPage = userService.getUsers(orgFlag,orgName,departmentId, department,userType, keyword, pageable);
        return ResponseModel.ok().setBody(userDTOPage);
    }

    @ApiOperation(value = "删除用户")
    @DeleteMapping("/manager/user")
    public ResponseModel deleteUser(@RequestParam String username) {
        userService.deleteUser(username);
        return ResponseModel.ok();
    }



    @ApiOperation(value = "获取所有校外访问申请审核信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "status", value = "审核状态0（待审核）,1（审核不通过）,2（通过）", paramType = "Integer", type = "query"),
            @ApiImplicitParam(name = "keyword", value = "查询关键字", paramType = "String", type = "query")
    })
    @GetMapping("/manager/valid")
    public ResponseModel getWaitValidList(@RequestParam(required = false) Integer status,
                                                      @RequestParam(required = false) String keyword,
                                                      @PageableDefault(value = 20, sort = {"gmtCreate"}, direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseModel.ok().setBody(userService.validList(status, keyword, pageable));
    }

    @ApiOperation(value = "审核证件照")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "被审核用户名", paramType = "String", type = "query"),
            @ApiImplicitParam(name = "pass", value = "审核通过/不通过", paramType = "Boolean", type = "query"),
            @ApiImplicitParam(name = "handlerName", value = "审核人", paramType = "String", type = "query"),
    })
    @PatchMapping("/manager/user/id-photo")
    public ResponseModel validateIdPhoto(@RequestParam String username,
                                         @RequestParam Boolean pass,
                                         @RequestParam(required = false) String handlerName) {
        userService.auditIdPhoto(username, pass, handlerName);
        return ResponseModel.ok().setMessage("审核成功");
    }
}
