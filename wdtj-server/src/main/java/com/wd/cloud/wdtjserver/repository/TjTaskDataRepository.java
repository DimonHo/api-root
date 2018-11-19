package com.wd.cloud.wdtjserver.repository;

import com.wd.cloud.wdtjserver.entity.TjDataPk;
import com.wd.cloud.wdtjserver.entity.TjTaskData;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
public interface TjTaskDataRepository extends JpaRepository<TjTaskData, TjDataPk> {

    TjTaskData findByIdOrgId(long orgId);
}
