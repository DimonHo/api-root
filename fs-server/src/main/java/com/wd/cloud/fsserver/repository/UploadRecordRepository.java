package com.wd.cloud.fsserver.repository;


import com.wd.cloud.fsserver.entity.UploadRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author He Zhigang
 * @date 2018/9/3
 * @Description:
 */
public interface UploadRecordRepository extends JpaRepository<UploadRecord, Long> {


    Page<UploadRecord> findByMissedAndAsynced(boolean missed, boolean asynced, Pageable pageable);

    /**
     * 丢失的文件列表
     *
     * @return
     */
    Page<UploadRecord> findByMissedIsTrue(Pageable pageable);

    /**
     * 未同步到hbase的记录
     *
     * @return
     */
    Page<UploadRecord> findByAsyncedIsFalse(Pageable pageable);

    Optional<UploadRecord> findByUnid(String nuid);

    Optional<UploadRecord> findByPathAndMd5(String path, String md5);

    /**
     * 查找有效文件
     *
     * @param nuid
     * @return
     */
    Optional<UploadRecord> findByUnidAndMissedIsFalse(String nuid);

    Optional<UploadRecord> findByPathAndFileName(String path, String fileName);

    Optional<UploadRecord> findByPathAndFileNameAndFileSize(String path, String fileName, Long fileSize);

    Optional<UploadRecord> findByPathAndFileNameAndFileSizeAndMissed(String path, String fileName, Long fileSize, boolean missed);
}
