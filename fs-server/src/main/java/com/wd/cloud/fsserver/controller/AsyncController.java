package com.wd.cloud.fsserver.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.fsserver.entity.UploadRecord;
import com.wd.cloud.fsserver.repository.UploadRecordRepository;
import io.swagger.annotations.Api;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/10/30
 * @Description:
 */
@Api(value = "文件同步接口", tags = {"hbase同步到mysql"})
@RestController
@RequestMapping("/")
public class AsyncController {

    private static final Log log = LogFactory.get();

    @Autowired
    HbaseTemplate hbaseTemplate;

    @Autowired
    UploadRecordRepository uploadRecordRepository;

    /**
     * 同步hbase中的数据至uploadRecord
     * @param tableName
     * @return
     */
    @GetMapping("/async")
    public ResponseModel hfToUploadRecord(@RequestParam String tableName) {
        hbaseTemplate.find(tableName, new Scan(), new RowMapper<String>() {
            @Override
            public String mapRow(Result result, int i) {
                List<Cell> cells = result.listCells();
                if (cells != null){
                    cells.stream().forEach(cell -> {
                        UploadRecord uploadRecord = new UploadRecord();
                        String fileName = null;
                        try {
                        fileName = Bytes.toString(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
                        String md5 = StrUtil.subBefore(fileName, ".", true);
                        String fileType = StrUtil.subAfter(fileName, ".", true);
                        byte[] fileByte = Arrays.copyOfRange(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                        uploadRecord.setFileName(fileName);
                        uploadRecord.setMd5(md5);
                        uploadRecord.setPath(tableName);
                        uploadRecord.setFileType(fileType);
                        uploadRecord.setFileSize((long) fileByte.length);
                        uploadRecord.setAsynced(true);
                        uploadRecordRepository.save(uploadRecord);
                        }catch (Exception e){
                            log.error("fileName:{}",fileName);
                        }
                    });
                }
                return "ok";
            }
        });
        return ResponseModel.ok().setBody("同步完成");
    }

    /**
     * 根据文件名获取unid
     * @param tableName
     * @param fileName
     * @return
     */
    @GetMapping("/getunid/{tableName}")
    public ResponseModel<String> getunid(@PathVariable String tableName, @RequestParam String fileName) {
        List<UploadRecord> uploadRecordList = hbaseTemplate.get(tableName, fileName, new RowMapper<List<UploadRecord>>() {
            @Override
            public List<UploadRecord> mapRow(Result result, int i) {
                return getUploadRecords(result, tableName);
            }
        });
        if (uploadRecordList == null || uploadRecordList.size() == 0){
            log.warn("未在hbase中找到文件{}",fileName);
        }else if(uploadRecordList.size()>1){
            log.warn("在hbase中找到多条符合fileName={}的记录",fileName);
        }else {
            return ResponseModel.ok().setBody(uploadRecordList.get(0).getUnid());
        }
        return ResponseModel.fail();
    }

    private List<UploadRecord> getUploadRecords(Result result, String tableName) {
        List<UploadRecord> uploadRecords = new ArrayList<>();
        List<Cell> cells = result.listCells();
        if (cells != null){
            cells.stream().forEach(cell -> {
                UploadRecord uploadRecord = new UploadRecord();
                String fileName = Bytes.toString(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
                String md5 = StrUtil.subBefore(fileName, ".", true);
                String fileType = StrUtil.subAfter(fileName, ".", true);
                byte[] fileByte = Arrays.copyOfRange(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                uploadRecord.setFileName(fileName);
                uploadRecord.setMd5(md5);
                uploadRecord.setPath(tableName);
                uploadRecord.setFileType(fileType);
                uploadRecord.setFileSize((long) fileByte.length);
                uploadRecord.setAsynced(true);
                try {
                    uploadRecord = uploadRecordRepository.save(uploadRecord);
                }catch (Exception e){
                    log.error(e,"fileName:{}");
                }
                uploadRecords.add(uploadRecord);
            });
        }
        return uploadRecords;
    }
}
