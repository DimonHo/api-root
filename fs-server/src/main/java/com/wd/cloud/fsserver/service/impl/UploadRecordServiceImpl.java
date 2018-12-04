package com.wd.cloud.fsserver.service.impl;

import com.wd.cloud.fsserver.entity.UploadRecord;
import com.wd.cloud.fsserver.repository.UploadRecordRepository;
import com.wd.cloud.fsserver.service.UploadRecordService;
import com.wd.cloud.fsserver.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @author He Zhigang
 * @date 2018/11/1
 * @Description:
 */
@Component("uploadRecordService")
public class UploadRecordServiceImpl implements UploadRecordService {

    @Autowired
    UploadRecordRepository uploadRecordRepository;

    @Override
    public Page<UploadRecord> getAll(boolean missed, boolean asynced, Pageable pageable) {
        return uploadRecordRepository.findByMissedAndAsynced(missed, asynced, pageable);
    }

    @Override
    public UploadRecord getOne(String unid) {
        return uploadRecordRepository.findByUnid(unid).orElse(null);
    }

    @Override
    public UploadRecord getNotMissed(String unid) {
        return uploadRecordRepository.findByUnidAndMissedIsFalse(unid).orElse(null);
    }

    @Override
    public UploadRecord getOne(String path, String fileMd5) {
        return uploadRecordRepository.findByPathAndMd5(path, fileMd5).orElse(null);
    }

    @Override
    public UploadRecord save(UploadRecord uploadRecord) {
        return uploadRecordRepository.save(uploadRecord);
    }

    @Override
    public UploadRecord save(String path, String fileName, String fileMd5, MultipartFile file) {
        //有则更新，没有则插入
        UploadRecord uploadRecord = uploadRecordRepository.findByPathAndMd5(path, fileMd5).orElse(new UploadRecord());
        uploadRecord.setPath(path)
                .setFileName(fileName)
                .setMd5(fileMd5)
                .setFileType(FileUtil.getFileType(file))
                .setFileSize(file.getSize())
                .setMissed(false);
        return save(uploadRecord);
    }

    @Override
    public UploadRecord save(String path, String fileName, String fileMd5, File file) {
        //有则更新，没有则插入
        UploadRecord uploadRecord = uploadRecordRepository.findByPathAndMd5(path, fileMd5).orElse(new UploadRecord());
        uploadRecord.setPath(path)
                .setFileName(fileName)
                .setMd5(fileMd5)
                .setFileType(FileUtil.getFileType(file))
                .setFileSize(file.length())
                .setMissed(false);
        return save(uploadRecord);
    }

    @Override
    public UploadRecord save(String path,String fileName,String fileMd5,String unid,File file){
        String md5 = FileUtil.fileMd5(file);
        //有则更新，没有则插入
        UploadRecord uploadRecord = uploadRecordRepository.findByPathAndMd5(path, md5).orElse(new UploadRecord());
        uploadRecord.setPath(path)
                .setFileName(fileName)
                .setMd5(md5)
                .setFileType(FileUtil.getFileType(file))
                .setFileSize(file.length())
                .setMissed(false)
                .setUnid(unid);
        return save(uploadRecord);
    }

    @Override
    public UploadRecord save(String path, String fileName, MultipartFile file) throws IOException {
        String md5 = FileUtil.fileMd5(file);
        //有则更新，没有则插入
        UploadRecord uploadRecord = uploadRecordRepository.findByPathAndMd5(path, md5).orElse(new UploadRecord());
        uploadRecord.setPath(path)
                .setFileName(fileName)
                .setMd5(FileUtil.fileMd5(file))
                .setFileType(FileUtil.getFileType(file))
                .setFileSize(file.getSize())
                .setMissed(false);
        return save(uploadRecord);
    }

    @Override
    public UploadRecord save(String path, String fileName, File file) {
        String md5 = FileUtil.fileMd5(file);
        //有则更新，没有则插入
        UploadRecord uploadRecord = uploadRecordRepository.findByPathAndMd5(path, md5).orElse(new UploadRecord());
        uploadRecord.setPath(path)
                .setFileName(fileName)
                .setMd5(md5)
                .setFileType(FileUtil.getFileType(file))
                .setFileSize(file.length())
                .setMissed(false);
        return save(uploadRecord);
    }

    @Override
    public Page<UploadRecord> getMissedList(Pageable pageable) {
        return uploadRecordRepository.findByMissedIsTrue(pageable);
    }

    @Override
    public Page<UploadRecord> getNotAsyncList(Pageable pageable) {
        return uploadRecordRepository.findByAsyncedIsFalse(pageable);
    }
}
