package com.wd.cloud.uoserver.repository;

import com.wd.cloud.uoserver.pojo.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 * @Author: He Zhigang
 * @Date: 2019/3/18 15:01
 * @Description:
 */
public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {

    Optional<Permission> findByUsername(String username);

    Optional<Permission> findByUsernameAndType(String username,Integer type);

}
