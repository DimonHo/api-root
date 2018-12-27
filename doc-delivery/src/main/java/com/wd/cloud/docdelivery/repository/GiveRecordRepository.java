package com.wd.cloud.docdelivery.repository;

import com.wd.cloud.docdelivery.entity.GiveRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/5/22
 * @Description:
 */
public interface GiveRecordRepository extends JpaRepository<GiveRecord, Long> {

    List<GiveRecord> findByHelpRecordIdAndAuditStatusNot(Long helpRecordId,Integer auditStatus);

    /**
     * 查询待审核记录
     *
     * @param id
     * @param auditStatus
     * @return
     */
    GiveRecord findByIdAndAuditStatus(Long id, int auditStatus);

    /**
     * 审核记录
     *
     * @param giverId
     * @param auditStatus
     * @return
     */
    GiveRecord findByGiverIdAndAuditStatus(Long giverId, int auditStatus);

    /**
     * 特定状态的应助记录
     *
     * @param helpRecordId
     * @param auditStatus
     * @param giverId
     * @return
     */
    GiveRecord findByHelpRecordIdAndAuditStatusAndGiverId(Long helpRecordId, int auditStatus, long giverId);

    /**
     * 取消应助，删除应助记录
     *
     * @param helpRecordId
     * @param auditStatus
     * @param giverId
     * @return
     */
    void deleteByHelpRecordIdAndAuditStatusAndGiverId(Long helpRecordId, int auditStatus, long giverId);

    /**
     * 特定应助类型的应助记录
     *
     * @param helpRecordId
     * @param auditStatus
     * @param giverType
     * @return
     */
    GiveRecord findByHelpRecordIdAndAuditStatusAndGiverType(Long helpRecordId, int auditStatus, int giverType);

    /**
     * 已审核通过的 或 非用户应助的应助记录
     *
     * @param helpRecordId
     * @return
     */
    @Query(value = "select * FROM give_record WHERE help_record_id = ?1 AND (audit_status = 1 OR giver_type <> 2)", nativeQuery = true)
    GiveRecord findByHelpRecordId(Long helpRecordId);

    GiveRecord findByHelpRecordIdAndAuditStatusEquals(Long helpRecordId, Integer status);

    @Query(value = "select * FROM give_record WHERE giver_type = 2 AND doc_file_id IS NULL AND 15 < TIMESTAMPDIFF(MINUTE, gmt_create, now())", nativeQuery = true)
    List<GiveRecord> findTimeOutRecord();

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM give_record WHERE giver_type = 2 AND doc_file_id IS NULL AND 15 < TIMESTAMPDIFF(MINUTE, gmt_create, now())", nativeQuery = true)
    List<GiveRecord> deleteTimeOutRecord();

    /**
     * 我的应助
     *
     * @param giverId
     * @return
     */
    long countByGiverId(Long giverId);

}
