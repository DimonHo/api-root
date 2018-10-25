package com.wd.cloud.resourceserver.repository;

import com.wd.cloud.resourceserver.entity.UploadRecord;
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

    Optional<UploadRecord> findByTargetAndPathAndFileNameAndFileSizeAndMissed(String target, String path, String fileName, Long fileSize, boolean missed);
}
