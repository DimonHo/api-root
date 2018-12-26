package com.wd.cloud.docdelivery.repository;

import com.wd.cloud.docdelivery.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author Wu Qilong
 * @date 2018/12/22
 */
public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {

    Permission findByOrgIdAndRule(Long orgId, Integer rule);

    Permission findByOrgIdIsNullAndRule(Integer rule);
}
