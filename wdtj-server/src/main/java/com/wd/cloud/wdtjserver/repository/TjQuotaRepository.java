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
     * @param orgId
     * @return
     */
    TjQuota findByOrgIdAndHistoryIsFalse(long orgId);

    /**
     * 查询机构日基数所有数据
     *
     * @param orgId
     * @return
     */
    Page<TjQuota> findByOrgId(long orgId, Pageable pageable);

    /**
     * 查询机构日基数历史数据
     *
     * @param orgId
     * @return
     */
    Page<TjQuota> findByOrgIdAndHistoryIsTrue(long orgId, Pageable pageable);

    /**
     * 查询有效日基数列表
     *
     * @return
     */
    Page<TjQuota> findByHistoryIsFalse(Pageable pageable);

    Page<TjQuota> findByHistory(Boolean history, Pageable pageable);

    Page<TjQuota> findByOrgIdAndHistory(Long orgId, Boolean history, Pageable pageable);

    /**
     * 根据机构名称或创建用户模糊查询
     * @param orgName
     * @param createUser
     * @param pageable
     * @return
     */
    Page<TjQuota> findByOrgNameContainingOrCreateUserContaining(String orgName, String createUser, Pageable pageable);
}
