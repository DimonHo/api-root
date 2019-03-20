package com.wd.cloud.wdtjserver.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.WeightRandom;
import com.wd.cloud.wdtjserver.entity.TjHisQuota;
import com.wd.cloud.wdtjserver.entity.TjQuota;
import com.wd.cloud.wdtjserver.model.HisQuotaModel;
import com.wd.cloud.wdtjserver.model.QuotaModel;
import com.wd.cloud.wdtjserver.model.TotalModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author He Zhigang
 * @date 2018/11/13
 * @Description:
 */
public class ModelUtil {

    public static TjQuota build(QuotaModel quotaModel) {
        TjQuota tjQuota = null;
        if (quotaModel.getPvCount() != 0) {
            tjQuota = new TjQuota();
            tjQuota.setPvCount(quotaModel.getPvCount())
                    .setDcCount(quotaModel.getDcCount())
                    .setDdcCount(quotaModel.getDdcCount())
                    .setScCount(quotaModel.getScCount())
                    // 访问用户数介于pv量的10%-30%之间
                    .setUvCount(quotaModel.getUvCount() == 0 ? (int) Math.round(quotaModel.getPvCount() * RandomUtil.randomDouble(0.1, 0.3)) : quotaModel.getUvCount())
                    // 访问次数介于uv量的2倍到pv量之间
                    .setVvCount(quotaModel.getVvCount() == 0 ? RandomUtil.randomInt(tjQuota.getUvCount() * 2, quotaModel.getPvCount()) : quotaModel.getVvCount())
                    .setAvgTime(quotaModel.getAvgTime());
        }
        return tjQuota;
    }

    public static TjHisQuota build(HisQuotaModel hisQuotaModel) {
        TjHisQuota tjHisQuota = null;
        if (hisQuotaModel.getPvCount() != 0) {
            tjHisQuota = new TjHisQuota();
            tjHisQuota.setPvCount(hisQuotaModel.getPvCount())
                    .setDcCount(hisQuotaModel.getDcCount())
                    .setDdcCount(hisQuotaModel.getDdcCount())
                    .setScCount(hisQuotaModel.getScCount())
                    // 访问用户数介于pv量的10%-30%之间
                    .setUvCount(hisQuotaModel.getUvCount() == 0 ? (int) Math.round(hisQuotaModel.getPvCount() * RandomUtil.randomDouble(0.1, 0.3)) : hisQuotaModel.getUvCount())
                    // 访问次数介于uv量的2倍到pv量之间
                    .setVvCount(hisQuotaModel.getVvCount() == 0 ? RandomUtil.randomInt(tjHisQuota.getUvCount() * 2, hisQuotaModel.getPvCount()) : hisQuotaModel.getVvCount())
                    .setAvgTime(hisQuotaModel.getAvgTime())
                    .setBeginTime(DateUtil.parseDateForMinute(DateUtil.formatDateTime(hisQuotaModel.getBeginTime())))
                    .setEndTime(DateUtil.parseDateForMinute(DateUtil.formatDateTime(hisQuotaModel.getEndTime())));
        }
        return tjHisQuota;
    }

    public static Map<DateTime, TotalModel> createResultMap(List<WeightRandom.WeightObj<DateTime>> hoursWeightList, String orgFlag, String orgName) {
        Map<DateTime, TotalModel> hourTotalModelHashMap = new HashMap<>();
        hoursWeightList.forEach(hoursWeight -> {
            TotalModel hourTotalModel = new TotalModel();
            //设置orgFlag和hourDate
            hourTotalModel.setOrgFlag(orgFlag);
            hourTotalModel.setOrgName(orgName);
            hourTotalModel.setDate(hoursWeight.getObj());
            hourTotalModelHashMap.put(hoursWeight.getObj(), hourTotalModel);
        });
        return hourTotalModelHashMap;
    }
}
