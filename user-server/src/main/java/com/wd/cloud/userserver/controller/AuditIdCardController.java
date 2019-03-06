package com.wd.cloud.userserver.controller;


import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.userserver.entity.AuditIdCard;
import com.wd.cloud.userserver.service.AuditIdCardServer;
import io.swagger.annotations.Api;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Api(value = "审核记录controller", tags = {"审核记录接口"})
@RestController
@RequestMapping("/audit")
public class AuditIdCardController {

    @Autowired
    AuditIdCardServer auditIdCardServer;

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
    public ResponseModel<AuditIdCard>  give(@RequestParam(required = false) String userName,
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
        auditIdCardServer.give(userName,idPhoto,nickName,orgName,department,identity,departmentId,education,sex,entranceTime,email,permission);
        return ResponseModel.ok().setMessage("已成功记录一条信息");

    }

    @ApiOperation(value = "根据用户名获取详细信息")
    @ApiImplicitParam(name = "userName", value = "用户名", dataType = "String", paramType = "query")
    @GetMapping("/getUserName")
    public ResponseModel<AuditIdCard> getUserName(@RequestParam(required = false) String userName){
        AuditIdCard auditIdCard = auditIdCardServer.getUserName(userName);
        return ResponseModel.ok().setBody(auditIdCard);
    }

    @ApiOperation(value = "或者所有校外访问申请审核信息")
    @GetMapping("/findAll")
    public ResponseModel<List<AuditIdCard>> findAll(@RequestParam(required = false) Integer status,
                                                    @RequestParam(required = false) String keyword,
                                                    @PageableDefault(value = 20, sort = {"gmtCreate"}, direction = Sort.Direction.DESC) Pageable pageable){
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("status", status);
        param.put("keyword", keyword);
        Page<AuditIdCard> auditIdCards = auditIdCardServer.findAll(pageable,param);
        return ResponseModel.ok().setBody(auditIdCards);
    }

    @ApiOperation(value = "根据ID获取详细信息")
    @GetMapping("/findById")
    public ResponseModel<AuditIdCard> findById(Long id){
        AuditIdCard auditIdCard = auditIdCardServer.findById(id);
        return ResponseModel.ok().setBody(auditIdCard);
    }

    @ApiOperation(value = "审核通过")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "permission", value = "访问权限", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "handlerName", value = "操作人", dataType = "Integer", paramType = "query")
    })
    @PostMapping("/apply")
    public ResponseModel<AuditIdCard> apply(@RequestParam(required = false)Long id,
                                            @RequestParam(required = false)Integer permission,
                                            @RequestParam(required = false)String handlerName){
        auditIdCardServer.apply(id, permission, handlerName);
        return ResponseModel.ok().setMessage("审核已通过");
    }

    @ApiOperation(value = "审核未通过")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "permission", value = "访问权限", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "handlerName", value = "操作人", dataType = "Integer", paramType = "query")
    })
    @PostMapping("/notApply")
    public ResponseModel<AuditIdCard> notApply(@RequestParam(required = false)Long id,
                                            @RequestParam(required = false)Integer permission,
                                            @RequestParam(required = false)String handlerName){
        auditIdCardServer.notApply(id, permission, handlerName);
        return ResponseModel.ok().setMessage("审核已通过");
    }

}
