package com.wd.cloud.userserver.repository;

import com.wd.cloud.userserver.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * @author He Zhigang
 * @date 2019/1/16
 * @Description:
 */
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

    Optional<UserInfo> findByUserId(Long userId);

    UserInfo findByUserIdAndValidated(Long userId, boolean validated);

    @Query(value = "select * from user_info where user_id =?1",nativeQuery = true)
    UserInfo findUserId(Long userId);
}
