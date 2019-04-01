package com.wd.cloud.wdtjserver.repository;

import com.wd.cloud.wdtjserver.entity.TjQuota;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description: 日基数设置DAO
 */
public interface TjQuotaRepository extends JpaRepository<TjQuota, Long>, JpaSpecificationExecutor<TjQuota> {

    /**
     * 查询机构日基数有效设置,一个机构有且只有一个有效设置
     *
     * @param orgFlag
     * @return
     */
    TjQuota findByOrgFlagAndHistoryIsFalse(String orgFlag);

    /**
     * 查询机构日基数所有数据
     *
     * @param orgFlag
     * @return
     */
    Page<TjQuota> findByOrgFlag(String orgFlag, Pageable pageable);

    /**
     * 查询机构日基数历史数据
     *
     * @param orgFlag
     * @return
     */
    Page<TjQuota> findByOrgFlagAndHistoryIsTrue(String orgFlag, Pageable pageable);

    /**
     * 查询有效日基数列表
     *
     * @return
     */
    Page<TjQuota> findByHistoryIsFalse(Pageable pageable);

    Page<TjQuota> findByHistory(Boolean history, Pageable pageable);

    Page<TjQuota> findByOrgFlagAndHistory(String orgFlag, Boolean history, Pageable pageable);

}
