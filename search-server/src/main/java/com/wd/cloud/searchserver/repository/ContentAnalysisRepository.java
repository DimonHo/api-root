package com.wd.cloud.searchserver.repository;

import com.wd.cloud.searchserver.entity.ContentAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

/**
 * @author He Zhigang
 * @date 2018/12/19
 * @Description:
 */
public interface ContentAnalysisRepository extends JpaRepository<ContentAnalysis, Long> {

    @Query(value = "select t2.name as orgName, count(*) as scCount  from t_content_analysis t1,(select flag,name from t_org where name = ?1) t2 where t1.org_flag = t2.flag and date_format(time,?3) = date_format(?2,?3) and type = 1", nativeQuery = true)
    List<Map<String, Object>> findBySchoolScCount(String school, String time, String format);

    @Query(value = "select t2.name as orgName, count(*) as scCount  from t_content_analysis t1,t_org t2 where date_format(t1.time,?2) = date_format(?1,?2) and t1.type = 1 and t1.org_flag = t2.flag group by t1.org_flag", nativeQuery = true)
    List<Map<String, Object>> findAllSchoolScCount(String time, String format);
}
