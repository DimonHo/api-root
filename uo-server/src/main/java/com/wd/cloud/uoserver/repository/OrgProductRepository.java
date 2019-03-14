package com.wd.cloud.uoserver.repository;

import com.wd.cloud.uoserver.entity.OrgProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description:
 */
public interface OrgProductRepository extends JpaRepository<OrgProduct, Long> {

    List<OrgProduct> findByOrgId(Long orgId);
}
