package com.wd.cloud.wdtjserver.repository;

import com.wd.cloud.wdtjserver.entity.TjDataPk;
import com.wd.cloud.wdtjserver.entity.TjViewData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
public interface TjViewDataRepository extends JpaRepository<TjViewData, TjDataPk>, JpaSpecificationExecutor<TjViewData> {
    /**
     * 分组聚合
     *
     * @param orgFlag
     * @param beginDate
     * @param endDate
     * @return
     */
    @Query(value = "select sum(pv_count) pvCount,sum(sc_count) scCount,sum(dc_count) dcCount,sum(ddc_count) ddcCount, sum(visit_time) sumTime, sum(uv_count) uvCount, sum(vv_count) vvCount, date_format(tj_date ,?4) tjDate from tj_view_data where org_flag = ?1 and tj_date >= ?2 and tj_date <= ?3 group by tjDate order by tjDate", nativeQuery = true)
    List<Map<String, Object>> groupByTjDate(String orgFlag, String beginDate, String endDate, String format);

    @Modifying
    @Query(value = "delete from tj_view_data where org_flag = ?1 and tj_date >= ?2 and tj_date <= ?3", nativeQuery = true)
    int deleteByTjDate(String orgFlag, String beginDate, String endDate);

}
