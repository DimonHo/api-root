package com.wd.cloud.orgserver.repository;

import com.wd.cloud.orgserver.entity.OrgInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author He Zhigang
 * @date 2018/11/5
 * @Description:
 */
public interface OrgInfoRepository extends JpaRepository<OrgInfo,Long> {

    Optional<OrgInfo> findByDefaultFlag(String flag);

    Optional<OrgInfo> findBySpisFlag(String flag);

    Optional<OrgInfo> findByEduFlag(String flag);
}
