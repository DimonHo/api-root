package com.wd.cloud.uoserver.repository;

import com.wd.cloud.uoserver.pojo.entity.OrgIp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description:
 */
public interface OrgIpRepository extends JpaRepository<OrgIp, Long> {

    /**
     * 查询机构所有ip范围
     * @param orgFlag
     * @return
     */
    List<OrgIp> findByOrgFlag(String orgFlag);

    /**
     * 获取指定记录
     * @param orgFlag
     * @param id
     * @return
     */
    Optional<OrgIp> findByOrgFlagAndId(String orgFlag, Long id);

    /**
     * 删除一条记录
     * @param orgFlag
     * @param id
     */
    void deleteByOrgFlagAndId(String orgFlag,Long id);

    /**
     * 查询IP范围是否有重叠
     * @param beginNum
     * @param endNum
     * @return
     */
    @Query(value = "from OrgIp where (beginNumber <= ?1 and endNumber >=?1) or (beginNumber <= ?2 and endNumber >=?2)")
    List<OrgIp> findExists(BigInteger beginNum, BigInteger endNum);

    /**
     * 查找某个IP
     * @param ipNum
     * @return
     */
    @Query(value = "from OrgIp where beginNumber <= ?1 and endNumber >=?1")
    Optional<OrgIp> findExists(BigInteger ipNum);
}
