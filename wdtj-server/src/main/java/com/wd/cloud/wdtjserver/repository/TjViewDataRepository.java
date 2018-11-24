package com.wd.cloud.wdtjserver.repository;

import com.wd.cloud.wdtjserver.entity.TjDataPk;
import com.wd.cloud.wdtjserver.entity.TjViewData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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
     * @param orgId
     * @param beginDate
     * @param endDate
     * @return
     */
    @Query(value = "select sum(pv_count) pvCount,sum(sc_count) scCount,sum(dc_count) dcCount,sum(ddc_count) ddcCount, sum(visit_time) sumTime, sum(uv_count) uvCount, sum(uc_count) ucCount, date_format(tj_date ,?4) tjDate from tj_view_data where org_id = ?1 and tj_date > ?2 and tj_date< ?3 group by tjDate order by tjDate", nativeQuery = true)
    List<Map<String, Object>> groupByTjDate(long orgId, String beginDate, String endDate,String format);

}
