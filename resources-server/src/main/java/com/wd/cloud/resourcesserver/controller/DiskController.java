package com.wd.cloud.resourcesserver.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.resourcesserver.config.GlobalConfig;
import com.wd.cloud.resourcesserver.service.FileService;
import com.wd.cloud.resourcesserver.util.FileUtil;
import com.wd.cloud.resourcesserver.util.HttpHeaderUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @author He Zhigang
 * @date 2018/8/24
 * @Description:
 */
@Api(value = "文件上传", tags = {"文件上传至服务器磁盘接口"})
@RestController
@RequestMapping("/df")
public class DiskController {

    @Autowired
    FileService fileService;

    @Autowired
    GlobalConfig globalConfig;

    /**
     * 自定义上传
     *
     * @param file
     * @param dir
     * @return
     */
    @ApiOperation(value = "文件上传，返回MD5文件名")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dir", value = "文件上传目录", dataType = "String", paramType = "path")
    })
    @PostMapping("/{dir}")
    public ResponseModel<JSONObject> uploadMd5File(@NotNull MultipartFile file, @PathVariable String dir) {

        JSONObject jsonObject = new JSONObject();
        try {
            String fileName = FileUtil.fileMd5(file);
            boolean flag = fileService.saveToDisk(globalConfig.getResources() + dir, fileName, file);
            if (!flag) {
                return ResponseModel.serverErr("上传失败，请重试");
            }
            jsonObject.put("file", fileName);
        } catch (IOException e) {
            return ResponseModel.serverErr("上传失败，请重试");
        }
        return ResponseModel.ok(jsonObject);
    }

    /**
     * 自定义上传
     *
     * @param file
     * @param dir
     * @return
     */
    @ApiOperation(value = "文件上传，返回自定义文件名")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dir", value = "文件上传目录", dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "filename", value = "文件名称", dataType = "String", paramType = "path")
    })
    @PostMapping("/{dir}/{fileName}")
    public ResponseModel<JSONObject> uploadCustomFile(@PathVariable String dir,
                                                      @PathVariable String fileName,
                                                      @NotNull MultipartFile file) {
        JSONObject jsonObject = new JSONObject();
        String extName = StrUtil.subAfter(file.getOriginalFilename(), ".", true);
        String newFileName = fileName + "." + extName;
        try {
            boolean flag = fileService.saveToDisk(globalConfig.getResources() + dir, newFileName, file);
            if (!flag) {
                return ResponseModel.serverErr("上传失败，请重试");
            }
            jsonObject.put("file", newFileName);
        } catch (IOException e) {
            return ResponseModel.serverErr("上传失败，请重试");
        }
        return ResponseModel.ok(jsonObject);
    }

    @ApiOperation(value = "文件下载")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dir", value = "文件所在目录", dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "filename", value = "文件名称", dataType = "String", paramType = "path")
    })
    @GetMapping("/{dir}/{fileName}")
    public ResponseEntity downlowdFile(@PathVariable String dir, @PathVariable String fileName, HttpServletRequest request) throws UnsupportedEncodingException {
        File file = new File(globalConfig.getResources() + dir + "/" + fileName);
        if (file.exists()){
            return ResponseEntity
                    .ok()
                    .headers(HttpHeaderUtil.buildHttpHeaders(fileName, request))
                    .contentLength(file.length())
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(new FileSystemResource(file));
        }else{
            return ResponseEntity.notFound().build();
        }

    }


}
