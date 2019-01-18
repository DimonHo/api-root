package com.wd.cloud.docdelivery.repository;

import com.wd.cloud.docdelivery.entity.HelpRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

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

    HelpRecord findByHelperIdAndStatus(long helperId, int status);

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
    Page<HelpRecord> findByHelperIdAndStatus(long helperId, Integer status, Pageable pageable);

    /**
     * 根据求助邮箱查询
     *
     * @param helperEmail
     * @param pageable
     * @return
     */
    Page<HelpRecord> findByHelperEmail(String helperEmail, Pageable pageable);

    @Query(value = "select * FROM Help_record where helper_email = ?1 AND literature_id = ?2 AND 15 > TIMESTAMPDIFF(DAY, gmt_create, now())", nativeQuery = true)
    HelpRecord findByHelperEmailAndLiteratureId(String helperEmail, Long literatureId);

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

    Page<HelpRecord> findByStatusIn(int[] status, Pageable pageable);

    List<HelpRecord> findBySend(boolean send);


    /**
     * 不同状态的总量
     *
     * @param status
     * @return
     */
    long countByStatus(Integer status);

    /**
     * 今日求助总量
     *
     * @return
     */
    @Query(value = "select count(*) from help_record where TO_DAYS(gmt_create) = TO_DAYS(NOW())", nativeQuery = true)
    long todayTotal();

    /**
     * 今日用户求助量
     *
     * @param helperId
     * @return
     */
    @Query(value = "select count(*) from help_record where helper_id = ?1 and TO_DAYS(gmt_create) = TO_DAYS(NOW())", nativeQuery = true)
    long myTodayTotal(Long helperId);

    /**
     * 今日邮箱求助量
     *
     * @param email
     * @return
     */
    @Query(value = "select count(*) from help_record where helper_email = ?1 and TO_DAYS(gmt_create) = TO_DAYS(NOW())", nativeQuery = true)
    long myTodayTotal(String email);

    /**
     * 用户求助总量
     *
     * @param helperId
     * @return
     */
    long countByHelperId(Long helperId);

    /**
     * 邮箱求助总量
     *
     * @param helperEmail
     * @return
     */
    long countByHelperEmail(String helperEmail);

    /**
     * 统计某邮箱求助状态的数量
     *
     * @param helperEmail
     * @param status
     * @return
     */
    long countByHelperEmailAndStatus(String helperEmail, Integer status);

    /**
     * 统计某用户求助状态的数量
     *
     * @param userId
     * @param status
     * @return
     */
    long countByHelperIdAndStatus(Long userId, Integer status);

    List<HelpRecord> findByLiteratureId(Long literatureId);

    List<HelpRecord> findByLiteratureIdIn(List ids);

    HelpRecord findByHelperId(Long helperId);

    @Query(value = "select count(helper_email) from help_record where helper_email =?1",nativeQuery = true)
    int findByHelperEmailCount(String helperEmail);

    @Query(value = "select count(helper_email) from help_record where helper_email = ?1 and to_days(gmt_create) = to_days(now())",nativeQuery = true)
    int findByHelperEmailDay(String helperEmail);


}
