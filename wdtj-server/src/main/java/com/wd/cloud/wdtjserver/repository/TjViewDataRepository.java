package com.wd.cloud.wdtjserver.repository;

import com.wd.cloud.wdtjserver.entity.TjViewData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
public interface TjViewDataRepository extends JpaRepository<TjViewData, Long>,JpaSpecificationExecutor<TjViewData> {
    //按时数查询
    @Query(value = "SELECT tj,count(tj) as count FROM(SELECT substr(tj_date,1,13) tj FROM tj_view_data where tj_date >?1 and tj_date <?2 and org_id =?3) a GROUP BY a.tj",nativeQuery = true)
    List<Map<String,Object>> findByTjDateAndOrgIdTime(String beginDate, String endDate, long orgId);

    //按天数查询
    @Query(value = "SELECT tj,count(tj) as count FROM(SELECT substr(tj_date,1,10) tj FROM tj_view_data where tj_date >?1 and tj_date <?2 and org_id =?3) a GROUP BY a.tj",nativeQuery = true)
    List<Map<String,Object>> findByTjDateAndOrgIdDay(String beginDate,String endDate,long orgId);

    //按月数查询
    @Query(value = "SELECT tj,count(tj) as count FROM(SELECT substr(tj_date,1,7) tj FROM tj_view_data where tj_date >?1 and tj_date <?2 and org_id =?3) a GROUP BY a.tj",nativeQuery = true)
    List<Map<String,Object>> findByTjDateAndOrgIdMonth(String beginDate,String endDate,long orgId);

    //按年数查询
    @Query(value = "SELECT tj,count(tj) as count FROM(SELECT substr(tj_date,1,4) tj FROM tj_view_data where tj_date >?1 and tj_date <?2 and org_id =?3) a GROUP BY a.tj",nativeQuery = true)
    List<Map<String,Object>> findByTjDateAndOrgIdYear(String beginDate, String endDate, long orgId);

}
