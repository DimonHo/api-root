package com.wd.cloud.wdtjserver.repository;

import com.wd.cloud.wdtjserver.entity.TjOrg;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
public interface TjOrgRepository extends JpaRepository<TjOrg, Long> {
    TjOrg findByOrOrgId(long orgId);
}
