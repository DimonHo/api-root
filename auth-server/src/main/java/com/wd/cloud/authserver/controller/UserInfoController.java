package com.wd.cloud.authserver.controller;

import com.wd.cloud.authserver.entity.UserInfo;
import com.wd.cloud.authserver.service.UserInfoServer;
import com.wd.cloud.commons.enums.StatusEnum;
import com.wd.cloud.commons.model.ResponseModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * @author He Zhigang
 * @date 2019/1/15
 * @Description:
 */
@Slf4j
@Api(value = "上传证件照controller", tags = {"上传证件照接口"})
@RestController
@RequestMapping("/userInfo")
public class UserInfoController {
    @Autowired
    UserInfoServer userInfoServer;


    @ApiOperation(value = "上次证件照")
    @PostMapping("/idPhoto/{file}")
    public ResponseModel getIdPhoto(@NotNull MultipartFile file) {
        if (file == null) {
            return ResponseModel.fail(StatusEnum.DOC_FILE_EMPTY);
        }
        userInfoServer.sava(file);
        return ResponseModel.ok().setMessage("上传证件照成功!");
    }

    @ApiOperation(value = "审核证件照")
    @PatchMapping("/validated/{userId}")
    public ResponseModel getValidated(@PathVariable Long userId) {
        userInfoServer.getValidated(userId);
        return ResponseModel.ok().setMessage("审核通过。");
    }

    @ApiOperation(value = "获取所有信息")
    @GetMapping("/userInfo")
    public ResponseModel getFindAll(@RequestParam Long userId) {
        UserInfo userInfo = userInfoServer.getUserId(userId);
        return ResponseModel.ok().setBody(userInfo);
    }
}
