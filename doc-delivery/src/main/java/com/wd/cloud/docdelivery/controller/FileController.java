package com.wd.cloud.docdelivery.controller;

import cn.hutool.core.util.StrUtil;
import com.wd.cloud.apifeign.AuthServerApi;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.docdelivery.model.DownloadModel;
import com.wd.cloud.docdelivery.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author He Zhigang
 * @date 2018/6/12
 * @Description:
 */
@Api(value = "文件controller", tags = {"文件上传下载接口"})
@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    FileService fileService;

    /**
     * 文献下载
     *
     * @return
     */
    @ApiOperation(value = "求助文件下载")
    @ApiImplicitParam(name = "helpRecodeId", value = "求助记录ID", dataType = "Long", paramType = "path")
    @GetMapping("/download/{helpRecodeId}")
    public ResponseEntity download(@PathVariable Long helpRecodeId) {

        DownloadModel downloadModel = fileService.getDownloadFile(helpRecodeId);
        if (!downloadModel.getDocFile().exists()) {
            return ResponseEntity.notFound().build();
        }
        HttpHeaders headers = new HttpHeaders();
        String disposition = StrUtil.format("attachment; filename=\"{}\"", downloadModel.getDownloadFileName());
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", disposition);
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(downloadModel.getDocFile().length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new FileSystemResource(downloadModel.getDocFile()));
    }

    @Autowired
    AuthServerApi authServerApi;

    @GetMapping("/user/{userid}")
    public ResponseModel getUser(@PathVariable Long userid){
        return ResponseModel.ok(authServerApi.getUserInfo(userid).getBody());
    }
}