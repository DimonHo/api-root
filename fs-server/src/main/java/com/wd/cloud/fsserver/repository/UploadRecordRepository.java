package com.wd.cloud.fsserver.repository;


import com.wd.cloud.fsserver.entity.UploadRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author He Zhigang
 * @date 2018/9/3
 * @Description:
 */
public interface UploadRecordRepository extends JpaRepository<UploadRecord, Long> {

    /**
     * 丢失的文件列表
     *
     * @return
     */
    Optional<List<UploadRecord>> findByMissedTrue();

    /**
     * 未同步到hbase的记录
     *
     * @return
     */
    List<UploadRecord> findByAsyncedIsFalse();

    Optional<UploadRecord> findByUnid(String nuid);

    /**
     * 查找有效文件
     * @param nuid
     * @return
     */
    Optional<UploadRecord> findByUnidAndMissedIsFalse(String nuid);

    Optional<UploadRecord> findByPathAndFileNameAndFileSize(String path, String fileName, Long fileSize);

    Optional<UploadRecord> findByPathAndFileNameAndFileSizeAndMissed(String path, String fileName, Long fileSize, boolean missed);
}
