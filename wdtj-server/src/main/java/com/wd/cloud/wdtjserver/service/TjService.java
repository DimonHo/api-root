package com.wd.cloud.wdtjserver.service;

import com.wd.cloud.wdtjserver.entity.TjHisQuota;
import com.wd.cloud.wdtjserver.entity.TjOrg;
import com.wd.cloud.wdtjserver.entity.TjQuota;
import com.wd.cloud.wdtjserver.model.DateIntervalModel;
import com.wd.cloud.wdtjserver.model.HisQuotaModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
public interface TjService {

    /**
     * 保存/更新机构设置
     *
     * @param tjOrg
     * @return
     */
    TjOrg save(TjOrg tjOrg);

    /**
     * 模糊查询机构名称
     *
     * @param orgName
     * @return
     */
    Page<TjOrg> likeOrgName(String orgName, boolean history, Pageable pageable);

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
     * @return
     */
    Page<TjOrg> filterByQuota(Boolean showPv, Boolean showSc, Boolean showDc, Boolean showDdc, Boolean showAvgTime, Pageable pageable);


    /**
     * 保存/更新 机构日基数设置
     *
     * @param tjQuota
     * @return
     */
    TjQuota save(TjQuota tjQuota);


    /**
     *
     * @param orgId
     * @return
     */
    TjQuota findOrgQuota(Long orgId);

    /**
     * @param orgId
     * @return
     */
    Page<TjQuota> findOrgQuota(Long orgId,Boolean history, Pageable pageable);

    /**
     * @param history
     * @return
     */
    Page<TjQuota> findAll(Boolean history, Pageable pageable);

    /**
     * 保存/更新 机构历史数据
     *
     * @param tjHisQuota
     * @return
     */
    TjHisQuota save(TjHisQuota tjHisQuota);

    /**
     * 添加生成历史数据
     *
     * @param orgId
     * @param hisQuotaModels
     * @return
     */
    Map<String, DateIntervalModel> checkInterval(Long orgId, List<HisQuotaModel> hisQuotaModels);


    List<TjHisQuota> save(Long orgId, List<HisQuotaModel> hisQuotaModels);

    /**
     * 获取机构时间段内（时）的统计数据
     *
     * @param beginDate
     * @param endDate
     * @param orgId
     * @return
     */
    List<Map<String, Object>> findByTjDateAndOrgIdTime(Date beginDate, Date endDate, long orgId);

    /**
     * 获取机构时间段内（天）的统计数据
     *
     * @param beginDate
     * @param endDate
     * @param orgId
     * @return
     */
    List<Map<String, Object>> findByTjDateAndOrgIdDay(Date beginDate, Date endDate, long orgId);

    /**
     * 获取机构时间段内（月）的统计数据
     *
     * @param beginDate
     * @param endDate
     * @param orgId
     * @return
     */
    List<Map<String, Object>> findByTjDateAndOrgIdMonth(Date beginDate, Date endDate, long orgId);

    /**
     * 获取机构时间段内（年）的统计数据
     *
     * @param beginDate
     * @param endDate
     * @param orgId
     * @return
     */
    List<Map<String, Object>> findByTjDateAndOrgIdYear(Date beginDate, Date endDate, long orgId);

    /**
     * 查询日基数设置列表
     *
     * @return
     */
    List<TjQuota> findByHistoryIsFalse();


    /**
     * 历史统计数据查询
     *
     * @param tjHisQuota
     */
    void search(TjHisQuota tjHisQuota);

    /**
     * 获取一个历史设置记录
     *
     * @param hisId
     * @return
     */
    TjHisQuota get(Long hisId);

    /**
     * 查询机构的历史设置记录
     *
     * @param orgId
     * @return
     */
    List<TjHisQuota> findHisSettingByOrg(Long orgId);

    /**
     * 生成历史数据详情
     *
     * @param tjHisQuota
     * @return
     */
    boolean buildTjHisData(TjHisQuota tjHisQuota);


}
