package com.wd.cloud.uoserver.repository;

import com.wd.cloud.uoserver.pojo.entity.UserMsg;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author: He Zhigang
 * @Date: 2019/3/23 19:57
 * @Description:
 */
public interface UserMsgRepository extends JpaRepository<UserMsg, Long> {

}
