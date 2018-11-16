package com.wd.cloud.wdtjserver.utils;

import com.wd.cloud.wdtjserver.entity.TjQuota;
import com.wd.cloud.wdtjserver.entity.TjHisQuota;
import com.wd.cloud.wdtjserver.model.HisQuotaModel;
import com.wd.cloud.wdtjserver.model.QuotaModel;

/**
 * @author He Zhigang
 * @date 2018/11/13
 * @Description:
 */
public class ModelUtil {

    public static TjQuota build(QuotaModel quotaModel) {
        TjQuota tjQuota = new TjQuota();
        tjQuota.setPvCount(quotaModel.getPvCount())
                .setDcCount(quotaModel.getDcCount())
                .setDdcCount(quotaModel.getDdcCount())
                .setScCount(quotaModel.getScCount())
                .setAvgTime(quotaModel.getAvgTime());
        return tjQuota;
    }

    public static TjHisQuota build(HisQuotaModel hisQuotaModel) {
        TjHisQuota tjHisQuota = new TjHisQuota();
        tjHisQuota.setPvCount(hisQuotaModel.getPvCount())
                .setDcCount(hisQuotaModel.getDcCount())
                .setDdcCount(hisQuotaModel.getDdcCount())
                .setScCount(hisQuotaModel.getScCount())
                .setAvgTime(hisQuotaModel.getAvgTime())
                .setBeginTime(hisQuotaModel.getBeginTime())
                .setEndTime(hisQuotaModel.getEndTime());
        return tjHisQuota;
    }
}
