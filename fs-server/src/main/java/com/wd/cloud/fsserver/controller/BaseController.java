package com.wd.cloud.fsserver.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONObject;
import com.wd.cloud.commons.enums.StatusEnum;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.fsserver.entity.UploadRecord;
import com.wd.cloud.fsserver.model.FileModel;
import com.wd.cloud.fsserver.service.FileService;
import com.wd.cloud.fsserver.service.UploadRecordService;
import com.wd.cloud.fsserver.util.HttpHeaderUtil;
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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/10/29
 * @Description:
 */
@Api(value = "文件资源服务接口", tags = {"fs-server API"})
@RestController
@RequestMapping("/")
public class BaseController {

    @Autowired
    FileService fileService;

    @Autowired
    UploadRecordService uploadRecordService;

    @ApiOperation(value = "检查文件是否已存在", tags = {"文件上传"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dir", value = "文件上传目录", dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "fileMd5", value = "文件校验码", dataType = "String", paramType = "path")
    })
    @GetMapping("/check/{dir}/{fileMd5}")
    public ResponseModel checkFile(@PathVariable String dir,
                                   @PathVariable String fileMd5) {
        UploadRecord uploadRecord = uploadRecordService.getOne(dir, fileMd5);
        JSONObject jsonObject = new JSONObject();
        if (uploadRecord != null) {
            jsonObject.put("fileId", uploadRecord.getUnid());
            return ResponseModel.ok().setBody(jsonObject);
        }
        return ResponseModel.ok().setBody(null);
    }

    @ApiOperation(value = "检查文件是否已存在", tags = {"文件上传"})
    @ApiImplicitParam(name = "unid", value = "文件ID", dataType = "String", paramType = "path")
    @GetMapping("/check/{unid}")
    public ResponseModel<Boolean> checkFile(@PathVariable String unid) {
        UploadRecord uploadRecord = uploadRecordService.getOne(unid);
        if (uploadRecord != null) {
            return ResponseModel.ok().setBody(true);
        }
        return ResponseModel.ok().setBody(false);
    }


    /**
     * 普通上传
     *
     * @param file
     * @param dir
     * @return
     */
    @ApiOperation(value = "文件上传", tags = {"文件上传"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dir", value = "文件上传目录", dataType = "String", paramType = "path")
    })
    @PostMapping("/upload/{dir}")
    public ResponseModel<JSONObject> uploadFile(@PathVariable String dir,
                                                @NotNull MultipartFile file) {
        JSONObject jsonObject = new JSONObject();
        try {
            UploadRecord uploadRecord = fileService.save(dir, file);
            if (uploadRecord.isMissed()) {
                return ResponseModel.fail().setMessage("文件[" + file.getOriginalFilename() + "]上传失败");
            } else {
                jsonObject.put("fileId", uploadRecord.getUnid());
                return ResponseModel.ok().setBody(jsonObject);
            }
        } catch (Exception e) {
            return ResponseModel.fail(e).setMessage("文件[" + file.getOriginalFilename() + "]上传失败");
        }
    }

    /**
     * 普通多文件上传
     *
     * @param files
     * @param dir
     * @return
     */
    @ApiOperation(value = "多文件上传", tags = {"文件上传"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dir", value = "文件上传目录", dataType = "String", paramType = "path")
    })
    @PostMapping("/upload/mulit/{dir}")
    public ResponseModel<JSONObject> uploadFiles(@PathVariable String dir,
                                                 @NotNull MultipartFile[] files) {
        JSONObject jsonObject = new JSONObject();
        JSONObject success = new JSONObject();
        List<String> failed = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                UploadRecord uploadRecord = fileService.save(dir, file);
                if (uploadRecord.isMissed()) {
                    failed.add(file.getOriginalFilename());
                } else {
                    success.put(file.getOriginalFilename(), uploadRecord.getUnid());
                }
            } catch (Exception e) {
                failed.add(file.getOriginalFilename());
            }
        }
        jsonObject.put("successed", success);
        jsonObject.put("failed", failed);
        return ResponseModel.ok().setBody(jsonObject);
    }


    @ApiOperation(value = "文件下载", tags = {"文件获取"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "unid", value = "文件唯一码", dataType = "String", paramType = "path")
    })
    @GetMapping("/load/{unid}")
    public ResponseEntity<FileSystemResource> downloadFile(@PathVariable String unid,
                                                           HttpServletRequest request)
            throws UnsupportedEncodingException {
        File file = fileService.getFile(unid);
        if (file != null) {
            return ResponseEntity
                    .ok()
                    .headers(HttpHeaderUtil.buildBroserFileHttpHeaders(file.getName(), request))
                    .contentLength(file.length())
                    .contentType(MediaType.parseMediaType(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                    .body(new FileSystemResource(file));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @ApiOperation(value = "获取文件byte流", tags = {"文件获取"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "unid", value = "文件唯一码", dataType = "String", paramType = "path")
    })
    @GetMapping("/byte/{unid}")
    public ResponseModel<byte[]> getFileByte(@PathVariable String unid) {
        File file = fileService.getFile(unid);
        if (file != null) {
            return ResponseModel.ok().setMessage(file.getName()).setBody(FileUtil.readBytes(file));
        }
        return ResponseModel.fail(StatusEnum.NOT_FOUND);
    }
}
