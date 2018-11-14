package com.wd.cloud.wdtjserver.service;

import com.wd.cloud.wdtjserver.entity.TjDaySetting;
import com.wd.cloud.wdtjserver.entity.TjHisSetting;
import com.wd.cloud.wdtjserver.entity.TjOrg;
import com.wd.cloud.wdtjserver.entity.TjViewData;
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
    Page<TjOrg> filterByQuota(boolean showPv, boolean showSc, boolean showDc, boolean showDdc, boolean showAvgTime, Pageable pageable);


    /**
     * 保存/更新 机构日基数设置
     *
     * @param tjDaySetting
     * @return
     */
    TjDaySetting save(TjDaySetting tjDaySetting);

    /**
     * 保存/更新 机构历史数据
     *
     * @param tjHisSetting
     * @return
     */
    TjHisSetting save(TjHisSetting tjHisSetting);

    /**
     * 添加生成历史数据
     *
     * @param orgId
     * @param hisQuotaModels
     * @return
     */
    List<DateIntervalModel> saveTjHisSettings(Long orgId, List<HisQuotaModel> hisQuotaModels);


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
    List<TjDaySetting> findByHistoryIsFalse();


    /**
     * 历史统计数据查询
     *
     * @param tjHisSetting
     */
    void search(TjHisSetting tjHisSetting);


    List<TjViewData> buildTjHisData(TjHisSetting tjHisSetting);
}
