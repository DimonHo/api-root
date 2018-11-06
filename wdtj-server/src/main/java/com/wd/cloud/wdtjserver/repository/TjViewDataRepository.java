package com.wd.cloud.wdtjserver.repository;

import com.wd.cloud.wdtjserver.entity.TjViewData;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
public interface TjViewDataRepository extends JpaRepository<TjViewData, Long> {
    TjViewData findByOrOrgId(long orgId);
}
