package com.wd.cloud.fsserver.controller;

import cn.hutool.json.JSONObject;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.fsserver.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * @author He Zhigang
 * @date 2018/10/30
 * @Description:
 */
@Api(value = "图片资源服务接口", tags = {"fs-server API"})
@RestController
@RequestMapping("/image")
public class ImageController {

    FileService imageService;

    @ApiOperation(value = "图片上传")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dir", value = "图片上传目录", dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "fileName", value = "文件名称（非必传）", dataType = "String", paramType = "query")
    })
    @PostMapping("/upload/{dir}")
    public ResponseModel upload(@PathVariable String dir,
                                @RequestParam(required = false) String fileName,
                                @NotNull MultipartFile file) {
        JSONObject jsonObject = new JSONObject();

        return ResponseModel.ok().setBody(jsonObject);
    }
}
