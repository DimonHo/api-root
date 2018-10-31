package com.wd.cloud.fsserver.task;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.fsserver.config.GlobalConfig;
import com.wd.cloud.fsserver.entity.UploadRecord;
import com.wd.cloud.fsserver.repository.UploadRecordRepository;
import com.wd.cloud.fsserver.service.FileService;
import com.wd.cloud.fsserver.service.HbaseService;
import com.wd.cloud.fsserver.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

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
    private UploadRecordRepository uploadRecordRepository;
    @Autowired
    private HbaseService hbaseService;

    /**
     * 每天凌晨0点执行一次
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteGiveRecord() {
        //找出所有未同步的记录
        List<UploadRecord> uploadRecords = uploadRecordRepository.findByAsyncedIsFalse();
        if (uploadRecords != null) {
            uploadRecords.forEach(uploadRecord -> {
                // 获取磁盘上的文件
                File file = FileUtil.getFileFromDisk(globalConfig.getRootPath() + uploadRecord.getPath(), uploadRecord.getFileName());
                if (file.exists()) {
                    try {
                        // 同步至hbase中
                        hbaseService.saveToHbase(uploadRecord.getPath(), uploadRecord.getUnid(), file);
                        // 更新记录
                        uploadRecord.setAsynced(true);
                        uploadRecordRepository.save(uploadRecord);
                    } catch (Exception e) {
                        log.warn(e, "文件{}同步到hbase失败", uploadRecord.getUnid());
                    }
                }
            });
        }
    }
}
