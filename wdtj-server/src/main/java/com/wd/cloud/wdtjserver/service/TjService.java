package com.wd.cloud.wdtjserver.service;

import com.wd.cloud.wdtjserver.entity.TjDaySetting;
import com.wd.cloud.wdtjserver.entity.TjHisSetting;
import com.wd.cloud.wdtjserver.entity.TjOrg;

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
     * 模糊查询机构名称
     * @param orgName
     * @return
     */
    List<TjOrg> likeOrgName(String orgName);

    /**
     * 保存/更新机构设置
     * @param tjOrg
     * @return
     */
    TjOrg save(TjOrg tjOrg);

    /**
     * 保存/更新 机构日基数设置
     * @param tjDaySetting
     * @return
     */
    TjDaySetting save(TjDaySetting tjDaySetting);

    /**
     * 保存/更新 机构历史数据
     * @param tjHisSetting
     * @return
     */
    TjHisSetting save(TjHisSetting tjHisSetting);

    /**
     * 获取机构时间段内（时）的统计数据
     * @param beginDate
     * @param endDate
     * @param orgId
     * @return
     */
    List<Map<String,Object>> findByTjDateAndOrgIdTime(Date beginDate,Date endDate,long orgId);

    /**
     * 获取机构时间段内（天）的统计数据
     * @param beginDate
     * @param endDate
     * @param orgId
     * @return
     */
    List<Map<String,Object>> findByTjDateAndOrgIdDay(Date beginDate,Date endDate,long orgId);

    /**
     * 获取机构时间段内（月）的统计数据
     * @param beginDate
     * @param endDate
     * @param orgId
     * @return
     */
    List<Map<String,Object>> findByTjDateAndOrgIdMonth(Date beginDate,Date endDate,long orgId);

    /**
     * 获取机构时间段内（年）的统计数据
     * @param beginDate
     * @param endDate
     * @param orgId
     * @return
     */
    List<Map<String,Object>> findByTjDateAndOrgIdYear(Date beginDate, Date endDate, long orgId);

    /**
     * 查询日基数设置列表
     * @return
     */
    List<TjDaySetting> findByHistoryIsFalse();

    /**
     * 过滤机构设置信息
     * @param showPv
     * @param showSc
     * @param showDc
     * @param showDdc
     * @param showAvgTime
     * @return
     */
    List<TjOrg> findByBoole(boolean showPv,boolean showSc,boolean showDc,boolean showDdc,boolean showAvgTime);

    /**
     * 历史统计数据查询
     * @param tjHisSetting
     */
    void search(TjHisSetting tjHisSetting);

}
