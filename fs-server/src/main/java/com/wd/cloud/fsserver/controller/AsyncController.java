package com.wd.cloud.fsserver.controller;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.fsserver.service.HbaseService;
import com.wd.cloud.fsserver.service.UploadRecordService;
import com.wd.cloud.fsserver.task.AsyncTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author He Zhigang
 * @date 2018/10/30
 * @Description:
 */
@Api(value = "文件同步接口", tags = {"fs-server API"})
@RestController
@RequestMapping("/async")
public class AsyncController {

    private static final Log log = LogFactory.get();

    @Autowired
    UploadRecordService uploadRecordService;

    @Autowired
    HbaseService hbaseService;

    @Autowired
    AsyncTask asyncTask;

    @ApiOperation(value = "手动同步至hbase", tags = {"文件同步"})
    @GetMapping("/to")
    public ResponseModel fileToHbase() {
        asyncTask.asyncToHbase();
        return ResponseModel.ok();
    }

    /**
     * 同步hbase中的数据同步至uploadRecord
     *
     * @param tableName
     * @return
     */
    @ApiOperation(value = "将hbase中现有文件同步到upload记录中", tags = {"文件同步"})
    @ApiImplicitParam(name = "tableName", value = "hbase表名", dataType = "String", type = "path")
    @GetMapping("/from/{tableName}")
    public ResponseModel hfToUploadRecord(@PathVariable String tableName) {
        hbaseService.hfToUploadRecord(tableName);
        return ResponseModel.ok().setBody("同步完成");
    }

}
