package com.wd.cloud.fsserver.service.impl;

import cn.hutool.extra.mail.MailUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.fsserver.config.GlobalConfig;
import com.wd.cloud.fsserver.entity.UploadRecord;
import com.wd.cloud.fsserver.service.FileService;
import com.wd.cloud.fsserver.service.HbaseService;
import com.wd.cloud.fsserver.service.UploadRecordService;
import com.wd.cloud.fsserver.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @author He Zhigang
 * @date 2018/7/20
 * @Description:
 */
@Component("fileService")
public class FileServiceImpl implements FileService {

    private static final Log log = LogFactory.get();
    @Autowired
    GlobalConfig globalConfig;

    @Autowired
    HbaseService hbaseService;

    @Autowired
    UploadRecordService uploadRecordService;

    @Override
    public UploadRecord save(String dir, MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        String fileMd5 = FileUtil.fileMd5(file);
        String unid = FileUtil.buildFileUuid(dir, fileMd5);
        //如果文件已存在且missed不为true
        UploadRecord uploadRecord = uploadRecordService.getNotMissed(unid);
        if (uploadRecord != null) {
            log.info("文件记录：{} 已存在，unid={}", file.getName(), uploadRecord.getUnid());
            return uploadRecord;
        }
        try {
            FileUtil.saveToDisk(globalConfig.getRootPath() + dir, file, fileMd5);
        } catch (IOException e) {
            log.warn(e, "文件：{} 保存磁盘失败，尝试上传至hbase中。。。", fileName);
            MailUtil.sendHtml("hezhigang@hnwdkj.com",
                    "fs-server exception",
                    String.format("文件:%s保存失败，请检查磁盘是否已满", fileName));
            //如果磁盘已满，直接保存至hbase
            hbaseService.saveToHbase(dir, unid, file);
        }
        uploadRecord = uploadRecordService.save(dir, fileName, fileMd5, file);
        return uploadRecord;
    }

    @Override
    public File getFile(String unid) {
        File file = null;
        UploadRecord uploadRecord = uploadRecordService.getOne(unid);
        if (uploadRecord != null) {
            String md5FileName = FileUtil.buildFileName(uploadRecord.getMd5(), uploadRecord.getFileType());
            // 获取磁盘中的文件
            file = FileUtil.getFileFromDisk(globalConfig.getRootPath() + uploadRecord.getPath(), md5FileName);
            //如果在磁盘中没找到文件，则去hbase中去获取
            if (!file.exists()) {
                byte[] fileByte = hbaseService.getFileFromHbase(uploadRecord.getPath(), unid);
                if (fileByte != null && fileByte.length > 0) {
                    FileUtil.writeBytes(fileByte, file);
                    // 找到了文件，更新文件状态
                    if (uploadRecord.isMissed()) {
                        uploadRecord.setMissed(false);
                        uploadRecordService.save(uploadRecord);
                    }
                } else {
                    //没找到文件，更新状态
                    uploadRecord.setMissed(true);
                    uploadRecordService.save(uploadRecord);
                    file = null;
                }
            }
        }
        return file;
    }

}
