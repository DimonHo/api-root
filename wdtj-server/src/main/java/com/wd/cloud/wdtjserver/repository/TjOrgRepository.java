package com.wd.cloud.wdtjserver.repository;

import com.wd.cloud.wdtjserver.entity.TjOrg;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

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

    @Query(value = "select distinct(org_name) from tj_org where is_history = false", nativeQuery = true)
    List<String> distinctByOrgFlag();


    /**
     * 查询符合状态的记录
     *
     * @param history
     * @return
     */
    Page<TjOrg> findByHistory(boolean history, Pageable pageable);

    List<TjOrg> findByHistoryIsFalse();

    /**
     * 根据orgFlag查询生效的设置
     */
    TjOrg findByOrgFlagAndHistoryIsFalse(String orgFlag);

    /**
     * 查询机构所有的设置记录
     *
     * @param orgFlag
     * @return
     */
    TjOrg findByOrgFlag(String orgFlag);

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


}
