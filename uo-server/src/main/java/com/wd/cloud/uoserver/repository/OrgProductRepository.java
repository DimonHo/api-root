package com.wd.cloud.uoserver.repository;

import com.wd.cloud.uoserver.entity.OrgProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description:
 */
public interface OrgProductRepository extends JpaRepository<OrgProduct, Long> {

    List<OrgProduct> findByOrgId(Long orgId);

    OrgProduct findByOrgIdAndProductId(Long orgId,Long productId);


    @Query(value = "select * from org_product where org_id =?1 and status =?2 and to_days(end_date) > to_days(now())",nativeQuery = true)
    List<OrgProduct> findByOrgIdAndStatus(Long orgId,Integer status);

    @Query(value = "select * from org_product where org_id =?1 and to_days(end_date) < to_days(now())",nativeQuery = true)
    List<OrgProduct> notFindByOrgId(Long orgId);

    void deleteByOrgId(Long orgId);

}
