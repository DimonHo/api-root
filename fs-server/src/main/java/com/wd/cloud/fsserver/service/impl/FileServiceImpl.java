package com.wd.cloud.fsserver.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.fsserver.config.GlobalConfig;
import com.wd.cloud.fsserver.entity.UploadRecord;
import com.wd.cloud.fsserver.repository.UploadRecordRepository;
import com.wd.cloud.fsserver.service.FileService;
import com.wd.cloud.fsserver.service.HbaseService;
import com.wd.cloud.fsserver.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @author He Zhigang
 * @date 2018/7/20
 * @Description:
 */
@Service("fileService")
public class FileServiceImpl implements FileService {

    private static final Log log = LogFactory.get();
    @Autowired
    GlobalConfig globalConfig;

    @Autowired
    HbaseService hbaseService;

    @Autowired
    UploadRecordRepository uploadRecordRepository;

    @Override
    public UploadRecord getUploadRecord(String dir, String fileMd5) {
        String unid = FileUtil.buildFileUuid(dir, fileMd5);
        return getUploadRecord(unid);
    }

    @Override
    public UploadRecord getUploadRecord(String unid) {
        return uploadRecordRepository.findByUnidAndMissedIsFalse(unid).orElse(null);
    }

    @Override
    public UploadRecord save(String dir, String fileName, MultipartFile file) throws Exception {
        // 如果未自定义文件名，则使用文件本身文件名
        fileName = StrUtil.isBlank(fileName) ? file.getOriginalFilename() : fileName;
        String fileMd5 = FileUtil.fileMd5(file);
        String unid = FileUtil.buildFileUuid(dir, fileMd5);
        //如果文件已存在且missed为false
        UploadRecord uploadRecord = getUploadRecord(unid);
        if (uploadRecord != null) {
            log.info("文件记录：{} 已存在，unid={}", fileName, uploadRecord.getUnid());
            return uploadRecord;
        }
        try {
            FileUtil.saveToDisk(globalConfig.getRootPath() + dir, fileName, file);
        } catch (IOException e) {
            log.info("文件：{} 保存磁盘失败，尝试上传至hbase中。。。", fileName);
            MailUtil.sendHtml("hezhigang@hnwdkj.com", "fs-server exception", String.format("文件:{}保存失败，请检查磁盘是否已满", fileName));
            //如果磁盘已满，直接保存至hbase
            hbaseService.saveToHbase(dir, unid, file);
        }
        uploadRecord = saveUploadRecord(dir, fileName, fileMd5, file);
        return uploadRecord;
    }

    @Override
    public File getFile(String unid) {
        File file = null;
        UploadRecord uploadRecord = uploadRecordRepository.findByUnid(unid).orElse(null);
        if (uploadRecord != null) {
            // 获取磁盘中的文件
            file = FileUtil.getFileFromDisk(globalConfig.getRootPath() + uploadRecord.getPath(), uploadRecord.getFileName());
            //如果在磁盘中没找到文件，则去hbase中去获取
            if (!file.exists()) {
                byte[] fileByte = hbaseService.getFileFromHbase(uploadRecord.getPath(), unid);
                if (fileByte != null && fileByte.length > 0) {
                    FileUtil.writeBytes(fileByte, file);
                    // 找到了文件，更新文件状态
                    if (uploadRecord.isMissed()) {
                        uploadRecord.setMissed(false);
                        uploadRecordRepository.save(uploadRecord);
                    }
                } else {
                    //没找到文件，更新状态
                    uploadRecord.setMissed(true);
                    uploadRecordRepository.save(uploadRecord);
                    file = null;
                }
            }
        }
        return file;
    }

    /**
     * 保存文件上传记录
     *
     * @param path
     * @param file
     * @return
     */
    private UploadRecord saveUploadRecord(String path, String fileName, String fileMd5, MultipartFile file) {

        //有则更新，没有则插入
        UploadRecord uploadRecord = uploadRecordRepository.findByUnid(fileMd5).orElse(new UploadRecord());
        uploadRecord.setPath(path);
        uploadRecord.setFileName(fileName);
        uploadRecord.setMd5(fileMd5);
        uploadRecord.setFileType(FileUtil.getFileType(fileName, file));
        uploadRecord.setFileSize(file.getSize());
        uploadRecord.setMissed(false);
        return uploadRecordRepository.save(uploadRecord);
    }


}
