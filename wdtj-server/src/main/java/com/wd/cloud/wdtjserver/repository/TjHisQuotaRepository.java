package com.wd.cloud.wdtjserver.repository;

import com.wd.cloud.wdtjserver.entity.TjHisQuota;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
public interface TjHisQuotaRepository extends JpaRepository<TjHisQuota, Long>, JpaSpecificationExecutor<TjHisQuota> {

    /**
     * 机构历史所有数据
     *
     * @param orgFlag
     * @return
     */
    Page<TjHisQuota> findByOrgFlag(String orgFlag, Pageable pageable);

    List<TjHisQuota> findByOrgFlag(String orgFlag);

    /**
     * @param orgFlag
     * @param locked
     * @return
     */
    Page<TjHisQuota> findByOrgFlagAndLocked(String orgFlag, boolean locked, Pageable pageable);

    /**
     * @param orgFlag
     * @param history
     * @return
     */
    Page<TjHisQuota> findByOrgFlagAndHistory(String orgFlag, boolean history, Pageable pageable);

}
