package com.wd.cloud.docdelivery.repository;

import com.wd.cloud.docdelivery.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * @author Wu Qilong
 * @date 2018/12/22
 */
public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {

    Permission findByOrgIdAndRule(Long orgId, Integer rule);

    Permission findByOrgIdIsNullAndRule(Integer rule);

    @Query(value = "select * from permission where org_id = ?1 and rule = ?2",nativeQuery = true)
    Permission getOrgIdAndRule(Long orgId,int rule);

}
