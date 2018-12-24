package com.wd.cloud.docdelivery.repository;

import com.wd.cloud.docdelivery.entity.HelpRecord;
import com.wd.cloud.docdelivery.entity.Literature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * @author He Zhigang
 * @date 2018/5/7
 * @Description:
 */
public interface HelpRecordRepository extends JpaRepository<HelpRecord, Long>, JpaSpecificationExecutor<HelpRecord> {

    /**
     * 查询
     *
     * @param id
     * @param status
     * @return
     */
    HelpRecord findByIdAndStatus(long id, int status);

    HelpRecord findByIdAndStatusNot(long id, int status);

    HelpRecord findByIdAndStatusIn(long id, int[] status);

    HelpRecord findByIdAndStatusNotIn(long id, int[] status);

    List<HelpRecord> findByHelperEmailAndGmtCreateAfter(String email, Date date);

    /**
     * 统计一个机构某个时间内的求助数量
     *
     * @param orgName
     * @param createDate
     * @param format     date_format(date,"%Y-%m-%d %H:%i:%s")
     * @return
     */
    @Query(value = "select helper_scname as orgName, count(*) as ddcCount from help_record where helper_scname=?1 and date_format(gmt_create,?3) = date_format(?2,?3)", nativeQuery = true)
    List<Map<String, Object>> findByOrgNameDdcCount(String orgName, String createDate, String format);

    @Query(value = "select helper_scname as orgName, count(*) as ddcCount from help_record where date_format(gmt_create,?2) = date_format(?1,?2) group by helper_scname", nativeQuery = true)
    List<Map<String, Object>> findAllDdcCount(String createDate, String format);

    /**
     * 根据求助用户ID查询
     *
     * @param helperId
     * @param pageable
     * @return
     */
    Page<HelpRecord> findByHelperIdAndStatus(long helperId,Integer status, Pageable pageable);

    /**
     * 根据求助邮箱查询
     *
     * @param helperEmail
     * @param pageable
     * @return
     */
    Page<HelpRecord> findByHelperEmail(String helperEmail, Pageable pageable);

    @Query("FROM HelpRecord where helperEmail = :helperEmail AND literature = :literature AND 15 > TIMESTAMPDIFF(DAY, gmtCreate, now())")
    HelpRecord findByHelperEmailAndLiterature(@Param("helperEmail") String helperEmail, @Param("literature") Literature literature);

    /**
     * 根据互助状态查询
     *
     * @param status
     * @param pageable
     * @return
     */
    Page<HelpRecord> findByStatus(int status, Pageable pageable);

    Page<HelpRecord> findByHelpChannelAndStatus(int helpChannel, int status, Pageable pageable);

    Page<HelpRecord> findByHelpChannelAndStatusIn(int helpChannel, int[] status, Pageable pageable);

    Page<HelpRecord> findByStatusIn(int[] status,Pageable pageable);

    List<HelpRecord> findBySend(boolean send);

    /**
     * 平台中求助量
     * @return
     */
    @Query(value = "select count(helper_id) from help_record",nativeQuery = true)
    int getAmount();

    /**
     * 平台传递成功率
     * @return
     */
    @Query(value = "select count(helper_id) from help_record where status =?1",nativeQuery = true)
    int getSuccessRate(int status);

    /**
     * 平台今日已求助量
     * @return
     */
    @Query(value = "select count(helper_id)  from help_record where TO_DAYS(gmt_create) = TO_DAYS(NOW())",nativeQuery = true)
    int getSameDay();

    /**
     * 我的求助
     * @param helperId
     * @return
     */
    @Query(value = "select count(helper_id) as forHelp from help_record where helper_id =?1",nativeQuery = true)
    int getForHelp(long helperId);



}
