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

    Permission findByOrgIdAndLevel(Long orgId, Integer level);

    Permission findByOrgIdIsNullAndLevel(Integer level);

    @Query(value = "select * from permission where org_id = ?1 and level = ?2", nativeQuery = true)
    Permission getOrgIdAndLevel(Long orgId, int level);

    Permission getByLevelAndOrgId(int level,long orgId);
}
