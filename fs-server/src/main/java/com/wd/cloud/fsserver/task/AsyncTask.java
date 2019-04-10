package com.wd.cloud.fsserver.task;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.fsserver.config.GlobalConfig;
import com.wd.cloud.fsserver.entity.UploadRecord;
import com.wd.cloud.fsserver.service.HbaseService;
import com.wd.cloud.fsserver.service.UploadRecordService;
import com.wd.cloud.fsserver.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author He Zhigang
 * @date 2018/10/30
 * @Description: 定时同步文件到hbase
 */
@Component
public class AsyncTask {

    private static final Log log = LogFactory.get();
    @Autowired
    GlobalConfig globalConfig;
    @Autowired
    UploadRecordService uploadRecordService;
    @Autowired
    HbaseService hbaseService;

    /**
     * 每天凌晨3点执行一次
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void asyncToHbase() {
        log.info("开始执行同步文件至hbase...");
        //找出所有未同步的记录
        Pageable pageable = PageRequest.of(0, 1000);
        Page<UploadRecord> uploadRecords = uploadRecordService.getNotAsyncList(pageable);
        log.info("共有{}个文件待同步", uploadRecords.getTotalElements());
        do {
            uploadRecords.getContent().forEach(this::asyncToHbase);
            uploadRecords = uploadRecordService.getNotAsyncList(uploadRecords.nextPageable());
        } while (uploadRecords.hasNext());
        log.info("执行同步文件至hbase完成");
    }

    private void asyncToHbase(UploadRecord uploadRecord) {
        // 获取磁盘上的文件
        File file = FileUtil.getFileFromDisk(globalConfig.getRootPath() + uploadRecord.getPath(), uploadRecord.getFileName());
        if (file.exists()) {
            try {
                // 同步至hbase中
                hbaseService.saveToHbase(uploadRecord.getPath(), uploadRecord.getFileName(), FileUtil.readBytes(file));
                // 更新记录
                uploadRecord.setAsynced(true);
                uploadRecordService.save(uploadRecord);
            } catch (Exception e) {
                log.error(e, "文件{}同步到hbase失败", uploadRecord.getFileName());
            }
        } else {
            uploadRecord.setMissed(true);
            uploadRecordService.save(uploadRecord);
            log.error("在磁盘找不到文件：{}", file.getName());
        }
    }
}
