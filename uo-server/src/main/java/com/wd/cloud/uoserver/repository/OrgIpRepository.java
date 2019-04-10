package com.wd.cloud.uoserver.repository;

import com.wd.cloud.commons.constant.CacheConstant;
import com.wd.cloud.uoserver.pojo.entity.OrgIp;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.repository.JpaRepository;

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
     *
     * @param orgFlag
     * @return
     */
    List<OrgIp> findByOrgFlag(String orgFlag);

    /**
     * 获取指定记录
     *
     * @param orgFlag
     * @param id
     * @return
     */
    Optional<OrgIp> findByOrgFlagAndId(String orgFlag, Long id);

    /**
     * 删除一条记录
     *
     * @param orgFlag
     * @param id
     */
    @Caching(evict = {
            @CacheEvict(value = CacheConstant.ORG_IP, allEntries = true),
            @CacheEvict(value = CacheConstant.ORG, key = "#orgFlag")
    })
    void deleteByOrgFlagAndId(String orgFlag, Long id);

}
