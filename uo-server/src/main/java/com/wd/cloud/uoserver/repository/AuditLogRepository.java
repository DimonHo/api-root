package com.wd.cloud.uoserver.repository;


import com.wd.cloud.uoserver.pojo.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long>, JpaSpecificationExecutor<AuditLog> {

    Optional<AuditLog> findByUsername(String userName);

    Optional<AuditLog> findByUsernameAndStatus(String userName, Integer status);

}
