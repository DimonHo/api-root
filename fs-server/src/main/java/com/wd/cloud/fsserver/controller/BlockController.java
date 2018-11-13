package com.wd.cloud.fsserver.controller;

import cn.hutool.core.lang.Console;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.fsserver.model.BlockFileModel;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

/**
 * @author He Zhigang
 * @date 2018/11/10
 * @Description: 文件断点续传接口
 */
@Api(value = "断点续传", tags = {"fs-server API"})
@RestController
@RequestMapping("/block")
public class BlockController {

    @PostMapping("/upload/{dir}")
    public ResponseModel upload(@PathVariable String dir,
                                @RequestParam String fileMd5,
                                @NotNull MultipartFile file,
                                HttpServletRequest request) {
        BlockFileModel blockFileModel = BlockFileModel.build(dir, fileMd5, file, request);
        Console.log(blockFileModel.toString());

        return ResponseModel.ok();
    }

    @PostMapping("/check")
    public ResponseModel checkChunk(@RequestParam String fileMd5,
                                    @RequestParam int chunkIndex,
                                    @RequestParam long chunkSize) {

        return ResponseModel.ok();
    }

    @PostMapping("/merge/{dir}")
    public ResponseModel mergeChunks(@RequestParam String fileMd5) {
        return ResponseModel.ok();
    }

}
