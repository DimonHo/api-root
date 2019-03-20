package com.wd.cloud.uoserver.repository;

import com.wd.cloud.uoserver.pojo.entity.IpRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description:
 */
public interface IpRangeRepository extends JpaRepository<IpRange, Long> {

    List<IpRange> findByOrgFlag(String orgFlag);

    /**
     * 获取指定记录
     * @param orgFlag
     * @param id
     * @return
     */
    Optional<IpRange> findByOrgFlagAndId(String orgFlag, Long id);

    IpRange findByOrgFlagAndBeginAndEnd(String orgFlag,String begin,String end);

    IpRange findByBeginAndEnd(String begin,String end);

    void deleteByOrgFlag(String orgFlag);

    void deleteByOrgFlagAndId(String orgFlag,Long id);

    /**
     * 查询IP范围是否有重叠
     * @param beginNum
     * @param endNum
     * @return
     */
    @Query(value = "from IpRange where (beginNumber <= ?1 and endNumber >=?1) or (beginNumber <= ?2 and endNumber >=?2)")
    List<IpRange> findExists(Long beginNum,Long endNum);
}
