package com.wd.cloud.wdtjserver.repository;

import com.wd.cloud.wdtjserver.entity.TjDataPk;
import com.wd.cloud.wdtjserver.entity.TjSpisData;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
public interface TjSpisDataRepository extends JpaRepository<TjSpisData, TjDataPk> {
    TjSpisData findByIdOrgId(Long orgId);
}
