package com.wd.cloud.wdtjserver.service;

import com.wd.cloud.wdtjserver.entity.TjHisQuota;
import com.wd.cloud.wdtjserver.entity.TjOrg;
import com.wd.cloud.wdtjserver.entity.TjQuota;
import com.wd.cloud.wdtjserver.model.HisQuotaModel;

import java.util.List;

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
     * 禁止机构
     * @param orgId
     * @return
     */
    boolean forbade(Long orgId);


    /**
     * 保存/更新 机构日基数设置
     *
     * @param tjQuota
     * @return
     */
    TjQuota save(TjQuota tjQuota);

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
     * @param orgId
     * @param hisQuotaModels
     * @return
     */
    List<TjHisQuota> save(Long orgId, List<HisQuotaModel> hisQuotaModels);
}
