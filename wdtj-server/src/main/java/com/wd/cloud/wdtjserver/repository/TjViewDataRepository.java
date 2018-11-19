package com.wd.cloud.wdtjserver.repository;

import com.wd.cloud.wdtjserver.entity.TjDataPk;
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
public interface TjViewDataRepository extends JpaRepository<TjViewData, TjDataPk>, JpaSpecificationExecutor<TjViewData> {
    /**
     * 按小时数聚合
     * @param beginDate
     * @param endDate
     * @param orgId
     * @return
     */
    @Query(value = "select sum(pv_count) pvCount,sum(sc_count) scCount,sum(dc_count) dcCount,sum(ddc_count) ddcCount, sum(visit_time) sumTime, sum(uv_count) uvCount, sum(uc_count) ucCount, date_format(tj_date ,\"%y-%m-%d %H\") tjDate from tj_view_data where org_id = ?1 and tj_date > ?2 and tj_date< ?3 group by tjDate order by tjDate", nativeQuery = true)
    List<Map<String, Object>> findByTjDateFromHours(long orgId, String beginDate, String endDate);

    /**
     * 按天数聚合
     */
    @Query(value = "select sum(pv_count) pvCount,sum(sc_count) scCount,sum(dc_count) dcCount,sum(ddc_count) ddcCount, sum(visit_time) sumTime, sum(uv_count) uvCount, sum(uc_count) ucCount, date_format(tj_date ,\"%y-%m-%d\") tjDate from tj_view_data where org_id = ?1 and tj_date > ?2 and tj_date< ?3 group by tjDate order by tjDate", nativeQuery = true)
    List<Map<String, Object>> findByTjDateFromDay(long orgId ,String beginDate, String endDate);

    /**
     * 按月聚合
     * @param orgId
     * @param beginDate
     * @param endDate
     * @return
     */
    @Query(value = "select sum(pv_count) pvCount,sum(sc_count) scCount,sum(dc_count) dcCount,sum(ddc_count) ddcCount, sum(visit_time) sumTime, sum(uv_count) uvCount, sum(uc_count) ucCount, date_format(tj_date ,\"%y-%m\") tjDate from tj_view_data where org_id = ?1 and tj_date > ?2 and tj_date< ?3 group by tjDate  order by tjDate", nativeQuery = true)
    List<Map<String, Object>> findByTjDateFromMonth(long orgId ,String beginDate, String endDate);

    /**
     * 按年聚合
     * @param orgId
     * @param beginDate
     * @param endDate
     * @return
     */
    @Query(value = "select sum(pv_count) pvCount,sum(sc_count) scCount,sum(dc_count) dcCount,sum(ddc_count) ddcCount, sum(visit_time) sumTime, sum(uv_count) uvCount, sum(uc_count) ucCount, date_format(tj_date ,\"%y\") tjDate from tj_view_data where org_id = ?1 and tj_date > ?2 and tj_date< ?3 group by tjDate order by tjDate", nativeQuery = true)
    List<Map<String, Object>> findByTjDateFromYear(long orgId , String beginDate, String endDate);

}
