package com.wd.cloud.fsserver.controller;

import com.wd.cloud.commons.enums.StatusEnum;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.fsserver.entity.UploadRecord;
import com.wd.cloud.fsserver.service.HbaseService;
import com.wd.cloud.fsserver.service.UploadRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

/**
 * @author He Zhigang
 * @date 2018/11/1
 * @Description:
 */
@Api(value = "数据管理接口", tags = {"fs-server API"})
@RestController
@RequestMapping("/manager")
public class ManagerController {
    @Autowired
    UploadRecordService uploadRecordService;

    @Autowired
    HbaseService hbaseService;


    @ApiOperation(value = "获取所有数据", tags = {"文件记录"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "missed", value = "是否丢失", dataType = "boolean", type = "query"),
            @ApiImplicitParam(name = "asynced", value = "是否已同步", dataType = "boolean", type = "query")
    })
    @GetMapping("/getall")
    public ResponseModel<UploadRecord> all(@RequestParam(required = false, defaultValue = "false") boolean missed,
                                           @RequestParam(required = false, defaultValue = "true") boolean asynced,
                                           @PageableDefault(sort = {"gmtCreate"}, direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseModel.ok().setBody(uploadRecordService.getAll(missed, asynced, pageable));
    }


    @ApiOperation(value = "根据文件ID获取记录", tags = {"文件记录"})
    @ApiImplicitParam(name = "unid", value = "文件Id", dataType = "String", type = "path")
    @GetMapping("/get/{unid}")
    public ResponseModel<UploadRecord> getOne(@PathVariable String unid) {
        UploadRecord uploadRecord = uploadRecordService.getOne(unid);
        if (uploadRecord != null) {
            return ResponseModel.ok().setBody(uploadRecord);
        }
        return ResponseModel.fail(StatusEnum.NOT_FOUND);
    }

    @ApiOperation(value = "根据path和文件md5值获取记录", tags = {"文件记录"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "path", value = "文件目录", dataType = "boolean", type = "path"),
            @ApiImplicitParam(name = "fileMd5", value = "文件MD5", dataType = "boolean", type = "path")
    })
    @GetMapping("/get/{path}/{fileMd5}")
    public ResponseModel<UploadRecord> get(@PathVariable String path, @PathVariable String fileMd5) {
        UploadRecord uploadRecord = uploadRecordService.getOne(path, fileMd5);
        if (uploadRecord != null) {
            return ResponseModel.ok().setBody(uploadRecord);
        }
        return ResponseModel.fail(StatusEnum.NOT_FOUND);
    }

//    @ApiOperation(value = "创建hbase表", tags = {"hbase管理"})
//    @ApiImplicitParam(name = "tableName", value = "表名", dataType = "String", type = "path")
//    @PutMapping("/create/{tableName}")
//    public ResponseModel creaetHbaseTable(@PathVariable String tableName) {
//        try {
//            hbaseService.createTable(tableName);
//            return ResponseModel.ok().setBody("表" + tableName + "创建成功");
//        } catch (IOException e) {
//            return ResponseModel.fail(e).setBody("表" + tableName + "创建失败");
//        }
//    }
//
//    @ApiOperation(value = "删除hbase表", tags = {"hbase管理"})
//    @ApiImplicitParam(name = "tableName", value = "表名", dataType = "String", type = "path")
//    @DeleteMapping("/drop/{tableName}")
//    public ResponseModel dropHbaseTable(@PathVariable String tableName) {
//        try {
//            hbaseService.dropTable(tableName);
//            return ResponseModel.ok().setBody("表" + tableName + "删除成功");
//        } catch (IOException e) {
//            return ResponseModel.fail(e).setBody("表" + tableName + "删除失败");
//        }
//    }
}
