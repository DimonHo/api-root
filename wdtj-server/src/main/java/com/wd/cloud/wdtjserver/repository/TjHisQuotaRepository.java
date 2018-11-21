package com.wd.cloud.wdtjserver.repository;

import com.wd.cloud.wdtjserver.entity.TjHisQuota;
import com.wd.cloud.wdtjserver.entity.TjOrg;
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
     * @param orgId
     * @return
     */
    Page<TjHisQuota> findByOrgId(long orgId, Pageable pageable);

    List<TjHisQuota> findByOrgId(long orgId);

    /**
     * @param orgId
     * @param locked
     * @return
     */
    Page<TjHisQuota> findByOrgIdAndLocked(Long orgId, boolean locked, Pageable pageable);

    /**
     * @param orgId
     * @param built
     * @return
     */
    Page<TjHisQuota> findByOrgIdAndBuilt(Long orgId, boolean built, Pageable pageable);

    /**
     * @param orgId
     * @param history
     * @return
     */
    Page<TjHisQuota> findByOrgIdAndHistory(Long orgId, boolean history, Pageable pageable);

    /**
     * 根据机构名称或创建用户模糊查询
     * @param orgName
     * @param createUser
     * @param pageable
     * @return
     */
    Page<TjHisQuota> findByOrgNameContainingOrCreateUserContaining(String orgName, String createUser, Pageable pageable);

}
