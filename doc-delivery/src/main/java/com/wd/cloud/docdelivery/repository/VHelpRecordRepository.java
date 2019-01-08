package com.wd.cloud.docdelivery.repository;

import com.wd.cloud.docdelivery.entity.VHelpRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author He Zhigang
 * @date 2019/1/5
 * @Description:
 */
public interface VHelpRecordRepository extends JpaRepository<VHelpRecord, Long>, JpaSpecificationExecutor<VHelpRecord> {
    /**
     * 查询求助记录
     *
     * @param helpRecordId
     * @return
     */
    VHelpRecord findByHelpRecordId(Long helpRecordId);
}
