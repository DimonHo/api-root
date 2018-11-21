package com.wd.cloud.wdtjserver.service;

import com.wd.cloud.wdtjserver.entity.TjHisQuota;
import com.wd.cloud.wdtjserver.model.DateIntervalModel;
import com.wd.cloud.wdtjserver.model.HisQuotaModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * @author He Zhigang
 * @date 2018/11/20
 * @Description:
 */
public interface HisQuotaService {


    /**
     * 保存/更新 机构历史数据
     *
     * @param tjHisQuota
     * @return
     */
    TjHisQuota save(TjHisQuota tjHisQuota);

    /**
     * 保存/更新 机构历史数据
     *
     * @param tjHisQuotas
     * @return
     */
    List<TjHisQuota> save(List<TjHisQuota> tjHisQuotas);

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
}
