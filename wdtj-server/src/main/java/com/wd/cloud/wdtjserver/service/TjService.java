package com.wd.cloud.wdtjserver.service;

import com.wd.cloud.wdtjserver.entity.TjHisQuota;
import com.wd.cloud.wdtjserver.entity.TjOrg;
import com.wd.cloud.wdtjserver.entity.TjQuota;
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
     * 模糊查询机构名称
     *
     * @param orgName
     * @return
     */
    Page<TjOrg> likeOrgName(String orgName, Boolean history, Pageable pageable);

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


    /**
     * @param orgId
     * @return
     */
    TjQuota findOrgQuota(Long orgId);

    /**
     * @param orgId
     * @return
     */
    Page<TjQuota> findOrgQuota(Long orgId, Boolean history, Pageable pageable);

    /**
     * @param history
     * @return
     */
    Page<TjQuota> findAll(Boolean history, Pageable pageable);


    /**
     * 添加生成历史数据
     *
     * @param orgId
     * @param hisQuotaModels
     * @return
     */
    Map<String, DateIntervalModel> checkInterval(Long orgId, List<HisQuotaModel> hisQuotaModels);


    TjHisQuota getHisQuota(Long hisId);

    /**
     * 获取机构历史设置记录
     *
     * @param orgId
     * @return
     */
    Page<TjHisQuota> getHisQuotaByOrg(Long orgId, Pageable pageable);

    /**
     * @param pageable
     * @return
     */
    Page<TjHisQuota> getAllHisQuota(Pageable pageable);

    /**
     * 生成历史数据详情
     *
     * @param tjHisQuota
     * @return
     */
    boolean buildTjHisData(TjHisQuota tjHisQuota);

    /**
     * @param orgId
     * @param beginTime
     * @param entTime
     * @return
     */
    Page<TjViewData> getViewDate(Long orgId, Date beginTime, Date entTime);

}
