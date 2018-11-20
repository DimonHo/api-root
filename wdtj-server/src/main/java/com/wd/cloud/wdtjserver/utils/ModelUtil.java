package com.wd.cloud.wdtjserver.utils;

import com.wd.cloud.wdtjserver.entity.TjHisQuota;
import com.wd.cloud.wdtjserver.entity.TjQuota;
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
                .setScCount(quotaModel.getScCount() == 0 ? (int) Math.round(quotaModel.getPvCount() * RandomUtil.randomDouble(0.5, 1)) : quotaModel.getScCount())
                .setUvCount(quotaModel.getUvCount() == 0 ? (int) Math.round(quotaModel.getPvCount() * RandomUtil.randomDouble(0.5, 1)) : quotaModel.getUvCount())
                .setUcCount(quotaModel.getUcCount() == 0 ? RandomUtil.randomInt(tjQuota.getUvCount(), quotaModel.getPvCount()) : quotaModel.getUcCount())
                .setAvgTime(quotaModel.getAvgTime());
        return tjQuota;
    }

    public static TjHisQuota build(HisQuotaModel hisQuotaModel) {
        TjHisQuota tjHisQuota = new TjHisQuota();
        tjHisQuota.setPvCount(hisQuotaModel.getPvCount())
                .setDcCount(hisQuotaModel.getDcCount())
                .setDdcCount(hisQuotaModel.getDdcCount())
                .setScCount(hisQuotaModel.getScCount() == 0 ? (int) Math.round(hisQuotaModel.getPvCount() * RandomUtil.randomDouble(0.3, 1)) : hisQuotaModel.getScCount())
                .setUvCount(hisQuotaModel.getUvCount() == 0 ? (int) Math.round(hisQuotaModel.getPvCount() * RandomUtil.randomDouble(0.3, 1)) : hisQuotaModel.getUvCount())
                .setUcCount(hisQuotaModel.getUcCount() == 0 ? RandomUtil.randomInt(tjHisQuota.getUvCount(), hisQuotaModel.getPvCount()) : hisQuotaModel.getUcCount())
                .setAvgTime(hisQuotaModel.getAvgTime())
                .setBeginTime(DateUtil.parseDateForMinute(DateUtil.formatDateTime(hisQuotaModel.getBeginTime())))
                .setEndTime(DateUtil.parseDateForMinute(DateUtil.formatDateTime(hisQuotaModel.getEndTime())));
        return tjHisQuota;
    }
}
