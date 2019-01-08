package com.wd.cloud.docdelivery.repository;

import com.wd.cloud.docdelivery.entity.GiveRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author He Zhigang
 * @date 2018/5/22
 * @Description:
 */
public interface GiveRecordRepository extends JpaRepository<GiveRecord, Long> {

    GiveRecord deleteByhelpRecordId(Long helpRecordId);

    List<GiveRecord> findByHelpRecordIdAndStatusNot(Long helpRecordId, Integer auditStatus);

    /**
     * 查询待审核记录
     *
     * @param id
     * @param status
     * @return
     */
    GiveRecord findByIdAndStatus(Long id, int status);

    /**
     * 审核记录
     *
     * @param giverId
     * @param status
     * @return
     */
    GiveRecord findByGiverIdAndStatus(Long giverId, int status);

    /**
     * 特定状态的应助记录
     *
     * @param helpRecordId
     * @param status
     * @param giverId
     * @return
     */
    GiveRecord findByHelpRecordIdAndStatusAndGiverId(Long helpRecordId, int status, long giverId);

    /**
     * 取消应助，删除应助记录
     *
     * @param helpRecordId
     * @param status
     * @param giverId
     * @return
     */
    void deleteByHelpRecordIdAndStatusAndGiverId(Long helpRecordId, int status, long giverId);

    /**
     * 特定应助类型的应助记录
     *
     * @param helpRecordId
     * @param status
     * @param giverType
     * @return
     */

    GiveRecord findByHelpRecordIdAndStatusAndType(Long helpRecordId, int status, int giverType);

    /**
     * 已审核通过的 或 非用户应助的应助记录
     *
     * @param helpRecordId
     * @return
     */
    @Query(value = "select * FROM give_record WHERE help_record_id = ?1 AND status = 6", nativeQuery = true)
    GiveRecord findByHelpRecordIdAndStatusSuccess(Long helpRecordId);

    List<GiveRecord> findByHelpRecordId(Long helpRecordId);

    /**
     * 查询指定状态的记录
     * @param helpRecordId
     * @param status
     * @return
     */
    Optional<GiveRecord> findByHelpRecordIdAndStatus(Long helpRecordId, Integer status);

    @Query(value = "select * FROM give_record WHERE type = 2 AND file_id IS NULL AND 15 < TIMESTAMPDIFF(MINUTE, gmt_create, now())", nativeQuery = true)
    List<GiveRecord> findTimeOutRecord();

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM give_record WHERE type = 2 AND file_id IS NULL AND 15 < TIMESTAMPDIFF(MINUTE, gmt_create, now())", nativeQuery = true)
    List<GiveRecord> deleteTimeOutRecord();

    /**
     * 我的应助
     *
     * @param giverId
     * @return
     */
    long countByGiverId(Long giverId);

    List<GiveRecord> findByFileId(String fileId);

}
