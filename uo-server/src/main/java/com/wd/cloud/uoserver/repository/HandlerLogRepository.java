package com.wd.cloud.uoserver.repository;


import com.wd.cloud.uoserver.pojo.entity.HandlerLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HandlerLogRepository extends JpaRepository<HandlerLog, Long> {

    Optional<HandlerLog> findByUsername(String userName);

    Optional<HandlerLog> findByUsernameAndType(String username,Integer type);
}
