package com.wd.cloud.uoserver.repository;

import com.wd.cloud.uoserver.entity.IpRange;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description:
 */
public interface IpRangeRepository extends JpaRepository<IpRange, Long> {

    List<IpRange> findByOrgId(Long orgId);

    IpRange findByOrgIdAndBeginAndEnd(Long orgId,String begin,String end);

    IpRange findByBeginAndEnd(String begin,String end);

    void deleteByOrgId(Long orgId);
}
