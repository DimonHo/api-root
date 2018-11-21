package com.wd.cloud.wdtjserver.repository;

import com.wd.cloud.wdtjserver.entity.TjOrg;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
public interface TjOrgRepository extends JpaRepository<TjOrg, Long>, JpaSpecificationExecutor<TjOrg> {

    /**
     * 根据获取所有满足条件的数据
     *
     * @param history
     * @param pageable
     * @return
     */
    Page<TjOrg> findAllByHistory(boolean history, Pageable pageable);


    /**
     * 查询符合状态的记录
     *
     * @param history
     * @return
     */
    Page<TjOrg> findByHistory(boolean history, Pageable pageable);

    /**
     * 根据orgId查询生效的设置
     */
    TjOrg findByOrgIdAndHistoryIsFalse(long orgId);

    /**
     * 查询机构所有的设置记录
     *
     * @param orgId
     * @return
     */
    TjOrg findByOrgId(long orgId);

    /**
     * 根据机构名称查询机构设置
     *
     * @param history
     * @param orgName
     * @return
     */
    Page<TjOrg> findByHistoryAndOrgNameLike(boolean history, String orgName, Pageable pageable);

    /**
     * 根据指标状态过滤
     *
     * @param showPv
     * @param showSc
     * @param showDc
     * @param showDdc
     * @param showAvgTime
     * @return
     */
    Page<TjOrg> findByHistoryIsFalseAndShowPvAndShowScAndShowDcAndShowDdcAndShowAvgTime(boolean showPv, boolean showSc, boolean showDc, boolean showDdc, boolean showAvgTime, Pageable pageable);

    /**
     * 根据机构名称或创建用户模糊查询
     * @param orgName
     * @param createUser
     * @param pageable
     * @return
     */
    Page<TjOrg> findByOrgNameContainingOrCreateUserContaining(String orgName, String createUser, Pageable pageable);
}
