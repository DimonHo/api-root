package com.wd.cloud.fsserver.controller;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.Header;
import com.wd.cloud.fsserver.model.FileModel;
import com.wd.cloud.fsserver.service.HbaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author He Zhigang
 * @date 2018/12/7
 * @Description:
 */
@Api(value = "hbase文件资源服务接口", tags = {"fs-server API"})
@RestController
@RequestMapping("/")
public class HbaseController {

    @Autowired
    HbaseService hbaseService;

    @ApiOperation(value = "文件下载", tags = {"文件获取"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tableName", value = "hbase表名", dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "fileName", value = "文件名称", dataType = "String", paramType = "path")
    })
    @GetMapping("/hf/{tableName}/{fileName}")
    public void downloadFile(@PathVariable String tableName,
                             @PathVariable String fileName,
                             HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        FileModel fileModel = hbaseService.getFileFromHbase(tableName, fileName);
        if (fileModel != null) {
            String filename = "";
            //判断是否是IE浏览器
            if (request.getHeader(Header.USER_AGENT.toString()).toLowerCase().contains("msie")) {
                filename = URLUtil.encode(fileModel.getName(), CharsetUtil.UTF_8);
            } else {
                filename = new String(fileModel.getName().getBytes(CharsetUtil.UTF_8), CharsetUtil.ISO_8859_1);
            }
            String disposition = StrUtil.format("attachment; filename=\"{}\"", filename);
            response.setHeader(Header.CACHE_CONTROL.toString(), "no-cache, no-store, must-revalidate");
            response.setHeader(Header.CONTENT_DISPOSITION.toString(), disposition);
            response.setHeader(Header.PRAGMA.toString(), "no-cache");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            OutputStream out = response.getOutputStream();
            out.write(fileModel.getBytes());
            out.close();
        }
    }
}
