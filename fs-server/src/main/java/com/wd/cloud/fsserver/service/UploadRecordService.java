package com.wd.cloud.fsserver.service;

import com.wd.cloud.fsserver.entity.UploadRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @author He Zhigang
 * @date 2018/11/1
 * @Description:
 */
public interface UploadRecordService {

    Page<UploadRecord> getAll(boolean isMissed, boolean isAnysced, Pageable pageable);

    UploadRecord getOne(String unid);

    UploadRecord getNotMissed(String unid);

    UploadRecord getOne(String path, String fileMd5);

    UploadRecord save(UploadRecord uploadRecord);

    UploadRecord save(String path, String fileName, String fileMd5, MultipartFile file);

    UploadRecord save(String path, String fileName, String fileMd5, File file);

    UploadRecord save(String path, String fileName, MultipartFile file) throws IOException;

    UploadRecord save(String path, String fileName, File file);

    Page<UploadRecord> getMissedList(Pageable pageable);

    Page<UploadRecord> getNotAsyncList(Pageable pageable);


}
