package com.wd.cloud.fsserver.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.fsserver.config.GlobalConfig;
import com.wd.cloud.fsserver.entity.UploadRecord;
import com.wd.cloud.fsserver.model.FileModel;
import com.wd.cloud.fsserver.repository.UploadRecordRepository;
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

    @Autowired
    UploadRecordRepository uploadRecordRepository;

    @Override
    public UploadRecord save(String dir, MultipartFile file) throws Exception {
        //源文件名
        String fileName = file.getOriginalFilename();
        //文件md5校验码
        String fileMd5 = FileUtil.fileMd5(file);
        //文件Id
        String unid = FileUtil.buildFileUnid(dir, fileMd5);
        UploadRecord uploadRecord = uploadRecordService.getOne(unid);
        if (uploadRecord == null || uploadRecord.isMissed()) {
            boolean isAsynced = false;
            boolean isMissed = true;
            try {
                FileUtil.saveToDisk(globalConfig.getRootPath() + dir, file, fileMd5);
                isMissed = false;
            } catch (IOException e) {
                log.warn(e, "文件：{} 保存磁盘失败，尝试上传至hbase中。。。", fileName);
                MailUtil.sendHtml("hezhigang@hnwdkj.com",
                        "fs-server exception",
                        String.format("文件:%s保存失败，请检查磁盘是否已满", fileName));
                //如果磁盘已满，直接保存至hbase
                isAsynced = hbaseService.saveToHbase(dir, fileMd5, file);
                isMissed = false;
            }
            if (uploadRecord == null) {
                uploadRecord = new UploadRecord();
            }
            uploadRecord.setAsynced(isAsynced)
                    .setMissed(isMissed)
                    .setFileName(fileName)
                    .setFileSize(file.getSize())
                    .setFileType(FileUtil.getFileType(file))
                    .setMd5(fileMd5)
                    .setPath(dir);
            uploadRecord = uploadRecordService.save(uploadRecord);
        } else {
            log.info("文件：{} 已存在，unid={}", fileName, uploadRecord.getUnid());
        }
        return uploadRecord;
    }

    @Override
    public File getFile(String unid) {
        File file = null;
        UploadRecord uploadRecord = uploadRecordService.getOne(unid);
        if (uploadRecord != null) {
            String fileName = getDiskFileName(uploadRecord);
            // 获取磁盘中的文件
            file = FileUtil.getFileFromDisk(globalConfig.getRootPath() + uploadRecord.getPath(), fileName);
            //如果在磁盘中没找到文件，则去hbase中去获取
            if (!file.exists()) {
                FileModel fileModel = hbaseService.getFileFromHbase(uploadRecord.getPath(), fileName);
                if (fileModel != null && fileModel.getBytes().length > 0) {
                    //保存文件到磁盘
                    FileUtil.writeBytes(fileModel.getBytes(), file);
                    // 找到了文件，更新文件状态
                    if (uploadRecord.isMissed()) {
                        uploadRecord.setMissed(false);
                        uploadRecordService.save(uploadRecord);
                    }
                } else {
                    if (!uploadRecord.isMissed()) {
                        //没找到文件，更新状态
                        uploadRecord.setMissed(true);
                        uploadRecordService.save(uploadRecord);
                    }
                    file = null;
                }
            }
        }
        return file;
    }




/**
 * 获取文件磁盘的文件名
 *
 * @param uploadRecord
 * @return
 */
private String getDiskFileName(UploadRecord uploadRecord){
        if(StrUtil.isBlank(uploadRecord.getFileType())){
        return uploadRecord.getMd5();
        }
        return uploadRecord.getMd5()+"."+uploadRecord.getFileType();
        }


@Override
public boolean checkChunkExists(String fileMd5,int chunkIndex,long chunkSize){
        return false;
        }

        }
