package com.wd.cloud.docdelivery.repository;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.wd.cloud.docdelivery.entity.HelpRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
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
     * 查询邮箱15天内求助某篇文献的记录
     * @param helperEmail
     * @param literatureId
     * @return
     */
    @Query(value = "select * FROM Help_record where helper_email = ?1 AND literature_id = ?2 AND 15 > TIMESTAMPDIFF(DAY, gmt_create, now())", nativeQuery = true)
    HelpRecord findByHelperEmailAndLiteratureId(String helperEmail, Long literatureId);


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
    long countToday();

    /**
     * 今日用户求助量
     *
     * @param helperId
     * @return
     */
    @Query(value = "select count(*) from help_record where helper_id = ?1 and TO_DAYS(gmt_create) = TO_DAYS(NOW())", nativeQuery = true)
    long countByHelperIdToday(Long helperId);

    /**
     * 今日邮箱求助量
     *
     * @param email
     * @return
     */
    @Query(value = "select count(*) from help_record where helper_email = ?1 and TO_DAYS(gmt_create) = TO_DAYS(NOW())", nativeQuery = true)
    long countByHelperEmailToday(String email);

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

    class SpecificationBuilder {
        public static Specification<HelpRecord> buildHelpRecord(Long helperId,String email,
                                                                List<Integer> channel,
                                                                List<Integer> status,
                                                                String keyword,
                                                                String beginTime,String endTime){
            return (Specification<HelpRecord>) (root, query, cb) -> {
                List<Predicate> list = new ArrayList<Predicate>();
                if (helperId != null) {
                    list.add(cb.equal(root.get("helperId"), helperId));
                }
                if (StrUtil.isNotBlank(email)) {
                    list.add(cb.equal(root.get("helperEmail"), email));
                }
                // 渠道过滤
                if (channel != null && channel.size() > 0) {
                    CriteriaBuilder.In<Integer> inChannel = cb.in(root.get("helpChannel"));
                    channel.forEach(inChannel::value);
                    list.add(inChannel);
                }
                // 状态过滤
                if (status != null && status.size() > 0) {
                    CriteriaBuilder.In<Integer> inStatus = cb.in(root.get("status"));
                    status.forEach(inStatus::value);
                    list.add(inStatus);
                }
                if (StrUtil.isNotBlank(keyword)) {
                    list.add(cb.or(cb.like(root.get("docTitle").as(String.class), "%" + keyword.trim() + "%"), cb.like(root.get("helperEmail").as(String.class), "%" + keyword.trim() + "%")));
                }
                if (StrUtil.isNotBlank(beginTime) && StrUtil.isNotBlank(endTime)) {
                    list.add(cb.between(root.get("gmtCreate").as(Date.class), DateUtil.parse(beginTime), DateUtil.parse(endTime)));
                }
                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            };
        }
    }
}
