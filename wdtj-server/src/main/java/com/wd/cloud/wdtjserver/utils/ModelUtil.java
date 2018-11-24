package com.wd.cloud.wdtjserver.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.WeightRandom;
import com.wd.cloud.wdtjserver.entity.TjHisQuota;
import com.wd.cloud.wdtjserver.entity.TjQuota;
import com.wd.cloud.wdtjserver.model.HisQuotaModel;
import com.wd.cloud.wdtjserver.model.HourTotalModel;
import com.wd.cloud.wdtjserver.model.QuotaModel;

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
        TjQuota tjQuota = new TjQuota();
        tjQuota.setPvCount(quotaModel.getPvCount())
                .setDcCount(quotaModel.getDcCount())
                .setDdcCount(quotaModel.getDdcCount())
                .setScCount(quotaModel.getScCount())
                .setUvCount(quotaModel.getUvCount() == 0 ? (int) Math.round(quotaModel.getPvCount() * RandomUtil.randomDouble(0.3, 1)) : quotaModel.getUvCount())
                .setUcCount(quotaModel.getUcCount() == 0 ? RandomUtil.randomInt(tjQuota.getUvCount(), quotaModel.getPvCount()) : quotaModel.getUcCount())
                .setAvgTime(quotaModel.getAvgTime());
        return tjQuota;
    }

    public static TjHisQuota build(HisQuotaModel hisQuotaModel) {
        TjHisQuota tjHisQuota = new TjHisQuota();
        tjHisQuota.setPvCount(hisQuotaModel.getPvCount())
                .setDcCount(hisQuotaModel.getDcCount())
                .setDdcCount(hisQuotaModel.getDdcCount())
                .setScCount(hisQuotaModel.getScCount())
                .setUvCount(hisQuotaModel.getUvCount() == 0 ? (int) Math.round(hisQuotaModel.getPvCount() * RandomUtil.randomDouble(0.3, 1)) : hisQuotaModel.getUvCount())
                .setUcCount(hisQuotaModel.getUcCount() == 0 ? RandomUtil.randomInt(tjHisQuota.getUvCount(), hisQuotaModel.getPvCount()) : hisQuotaModel.getUcCount())
                .setAvgTime(hisQuotaModel.getAvgTime())
                .setBeginTime(DateUtil.parseDateForMinute(DateUtil.formatDateTime(hisQuotaModel.getBeginTime())))
                .setEndTime(DateUtil.parseDateForMinute(DateUtil.formatDateTime(hisQuotaModel.getEndTime())));
        return tjHisQuota;
    }

    public static Map<DateTime, HourTotalModel> createResultMap(List<WeightRandom.WeightObj<DateTime>> hoursWeightList, Long orgId, String orgName) {
        Map<DateTime, HourTotalModel> hourTotalModelHashMap = new HashMap<>();
        hoursWeightList.forEach(hoursWeight -> {
            HourTotalModel hourTotalModel = new HourTotalModel();
            //设置orgId和hourDate
            hourTotalModel.setOrgId(orgId);
            hourTotalModel.setOrgName(orgName);
            hourTotalModel.setHourDate(hoursWeight.getObj());
            hourTotalModelHashMap.put(hoursWeight.getObj(), hourTotalModel);
        });
        return hourTotalModelHashMap;
    }
}
