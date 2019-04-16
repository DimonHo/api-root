package com.wd.cloud.fsserver.service.impl;

import cn.hutool.extra.mail.MailUtil;
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
        String srcName = file.getOriginalFilename();
        //文件md5校验码
        String md5 = FileUtil.fileMd5(file);
        String fileMd5Name = FileUtil.buildFileMd5Name(file, md5);
        UploadRecord uploadRecord = uploadRecordService.getOne(dir, md5);
        if (uploadRecord == null || uploadRecord.isMissed()) {
            if (uploadRecord == null) {
                uploadRecord = new UploadRecord();
            }
            log.info("正在保存[{}]至磁盘。。。", srcName);
            boolean isAsynced = false;
            boolean isMissed = true;
            try {
                File diskFile = FileUtil.saveToDisk(globalConfig.getRootPath() + dir, file);
                uploadRecord.setFileName(diskFile.getName());
                isMissed = false;
                log.info("保存[{}]至磁盘成功", srcName);
            } catch (IOException e) {
                log.warn(e, "文件：{} 保存磁盘失败，尝试上传至hbase中。。。", srcName);
                MailUtil.sendHtml("hezhigang@hnwdkj.com",
                        "fs-server exception",
                        String.format("文件:%s保存失败，请检查磁盘是否已满", srcName));
                //如果磁盘已满，直接保存至hbase
                isAsynced = hbaseService.saveToHbase(dir, file);
                isMissed = false;
                log.info("保存[{}]至Hbase成功", srcName);
            }

            uploadRecord.setAsynced(isAsynced)
                    .setMissed(isMissed)
                    .setFileName(fileMd5Name)
                    .setSrcName(srcName)
                    .setFileSize(file.getSize())
                    .setFileType(FileUtil.getFileType(file))
                    .setMd5(md5)
                    .setPath(dir);
            uploadRecord = uploadRecordService.save(uploadRecord);
        } else {
            log.info("文件：{} 已存在", srcName);
        }
        return uploadRecord;
    }

    @Override
    public File getFile(String unid) {
        File file = null;
        UploadRecord uploadRecord = uploadRecordService.getOne(unid);
        if (uploadRecord != null) {
            log.info("找到了文件上传记录[{}]", uploadRecord);
            // 获取磁盘中的文件
            file = FileUtil.getFileFromDisk(globalConfig.getRootPath() + uploadRecord.getPath(), uploadRecord.getFileName());
            //如果在磁盘中没找到文件，则去hbase中去获取
            if (!file.exists()) {
                log.warn("文件[{}]在磁盘中未找到！尝试去Hbase查找。。。", uploadRecord.getFileName());
                FileModel fileModel = hbaseService.getFileFromHbase(uploadRecord.getPath(), uploadRecord.getFileName());
                if (fileModel != null && fileModel.getBytes().length > 0) {
                    log.info("文件[{}]在Hbase查找成功。。。", uploadRecord.getFileName());
                    //保存文件到磁盘
                    FileUtil.writeBytes(fileModel.getBytes(), file);
                    // 找到了文件，更新文件状态
                    if (uploadRecord.isMissed()) {
                        uploadRecord.setMissed(false);
                        uploadRecordService.save(uploadRecord);
                    }
                } else {
                    log.warn("文件[{}]在Hbase中未找到！", uploadRecord.getFileName());
                    if (!uploadRecord.isMissed()) {
                        //没找到文件，更新状态
                        uploadRecord.setMissed(true);
                        uploadRecordService.save(uploadRecord);
                    }
                    file = null;
                }
            }
        } else {
            log.error("未找到文件上传记录[{}]", unid);
        }
        return file;
    }


    @Override
    public boolean checkChunkExists(String fileMd5, int chunkIndex, long chunkSize) {
        return false;
    }

}
