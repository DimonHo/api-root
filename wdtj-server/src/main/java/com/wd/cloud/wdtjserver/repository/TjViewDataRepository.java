package com.wd.cloud.wdtjserver.repository;

import com.wd.cloud.wdtjserver.entity.TjViewData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
public interface TjViewDataRepository extends JpaRepository<TjViewData, Long>,JpaSpecificationExecutor<TjViewData> {
    TjViewData findByOrgId(long orgId);


}
