package com.wd.cloud.wdtjserver.repository;

import com.wd.cloud.wdtjserver.entity.TjHisQuota;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
public interface TjHisQuotaRepository extends JpaRepository<TjHisQuota, Long> {

    /**
     * 机构历史所有数据
     * @param orgId
     * @return
     */
    List<TjHisQuota> findByOrgId(long orgId);

    /**
     *
     * @param orgId
     * @param locked
     * @return
     */
    List<TjHisQuota> findByOrgIdAndLocked(Long orgId, boolean locked);

    /**
     *
     * @param orgId
     * @param built
     * @return
     */
    List<TjHisQuota> findByOrgIdAndBuilt(Long orgId, boolean built);

    /**
     *
     * @param orgId
     * @param history
     * @return
     */
    List<TjHisQuota> findByOrgIdAndHistory(Long orgId, boolean history);

}
