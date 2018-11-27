package com.wd.cloud.wdtjserver.service;

import com.wd.cloud.wdtjserver.entity.TjOrg;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author He Zhigang
 * @date 2018/11/16
 * @Description:
 */
public interface SettingService {

    /**
     * 保存/更新机构设置
     *
     * @param tjOrg
     * @return
     */
    TjOrg save(TjOrg tjOrg);


    /**
     * 获取机构信息
     *
     * @param orgId
     * @return
     */
    TjOrg getOrgInfo(Long orgId);

    /**
     * 保存/更新机构设置
     *
     * @param orgId
     * @param showPv
     * @param showSc
     * @param showDc
     * @param showDdc
     * @param showAvgTime
     * @return
     */
    TjOrg saveTjOrg(long orgId, boolean showPv, boolean showSc, boolean showDc, boolean showDdc, boolean showAvgTime, String createUser);

    /**
     * 禁止/解除禁止机构
     *
     * @param orgId
     * @return
     */
    TjOrg forbade(Long orgId);

    /**
     * 模糊查询
     *
     * @param query
     * @return
     */
    Page<TjOrg> likeQuery(String query, Boolean history, Pageable pageable);

    /**
     * 获取已生效的机构设置列表
     *
     * @param pageable
     * @return
     */
    Page<TjOrg> getEnabledFromAll(Pageable pageable);

    /**
     * 获取历史设置列表
     *
     * @param pageable
     * @return
     */
    Page<TjOrg> getHistoryFromAll(Pageable pageable);

    /**
     * 获取所有机构设置列表
     *
     * @param pageable
     * @return
     */
    Page<TjOrg> getAll(Pageable pageable);

    /**
     * 过滤机构设置信息
     *
     * @param showPv
     * @param showSc
     * @param showDc
     * @param showDdc
     * @param showAvgTime
     * @param forbade
     * @return
     */
    Page<TjOrg> filterOrgByQuota(Boolean showPv, Boolean showSc, Boolean showDc, Boolean showDdc, Boolean showAvgTime, Boolean forbade, Pageable pageable);

}
