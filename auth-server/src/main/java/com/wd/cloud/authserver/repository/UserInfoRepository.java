package com.wd.cloud.authserver.repository;

import com.wd.cloud.authserver.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author He Zhigang
 * @date 2019/1/16
 * @Description:
 */
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

    Optional<UserInfo> findByUserId(Long userId);
}
