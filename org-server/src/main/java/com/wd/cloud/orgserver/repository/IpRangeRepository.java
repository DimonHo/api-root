package com.wd.cloud.orgserver.repository;

import com.wd.cloud.orgserver.entity.IpRange;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author He Zhigang
 * @date 2018/11/5
 * @Description:
 */
public interface IpRangeRepository extends JpaRepository<IpRange,Long> {
}
