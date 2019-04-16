package com.wd.cloud.uoserver.controller;

import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.uoserver.service.TjService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: He Zhigang
 * @Date: 2019/3/20 19:13
 * @Description:
 */
@Slf4j
@RestController
public class TjController {

    @Autowired
    TjService tjService;

    @ApiOperation(value = "统计机构用户数", tags = {"统计"})
    @GetMapping("/tj/org")
    public ResponseModel tjOrgUser() {
        return ResponseModel.ok().setBody(tjService.tjOrgUser());
    }

    @ApiOperation(value = "统计院系用户数", tags = {"统计"})
    @ApiImplicitParam(name = "orgFlag", value = "机构标识", paramType = "String", type = "query")
    @GetMapping("/tj/org/dept")
    public ResponseModel tjOrgUser(@RequestParam String orgFlag) {
        return ResponseModel.ok().setBody(tjService.tjOrgDeptUser(orgFlag));
    }
}
