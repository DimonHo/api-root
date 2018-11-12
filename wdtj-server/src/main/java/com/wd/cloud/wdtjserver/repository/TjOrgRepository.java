package com.wd.cloud.wdtjserver.repository;

import com.wd.cloud.wdtjserver.entity.TjOrg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
public interface TjOrgRepository extends JpaRepository<TjOrg, Long> {
    //根据orgId查询
    TjOrg findByOrgIdAndHistoryIsFalse(long orgId);
    TjOrg findByOrgId(long orgId);

    List<TjOrg> findByOrgNameLike(String name);

    List<TjOrg> findByShowPvAndShowScAndShowDcAndShowDdcAndShowAvgTime(boolean showPv,boolean showSc,boolean showDc,boolean showDdc,boolean showAvgTime);


}
