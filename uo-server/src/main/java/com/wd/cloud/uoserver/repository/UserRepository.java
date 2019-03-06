package com.wd.cloud.uoserver.repository;

import com.wd.cloud.uoserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description:
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 用户名查询
     *
     * @param username
     * @return
     */
    Optional<User> findByUsername(String username);


    /**
     * 用户名查询
     * @param username
     * @param validated
     * @return
     */
    Optional<User> findByUsernameAndValidated(String username, Boolean validated);
}
