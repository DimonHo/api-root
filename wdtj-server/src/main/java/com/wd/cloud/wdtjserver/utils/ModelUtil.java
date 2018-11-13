package com.wd.cloud.wdtjserver.utils;

import com.wd.cloud.wdtjserver.entity.TjDaySetting;
import com.wd.cloud.wdtjserver.entity.TjHisSetting;
import com.wd.cloud.wdtjserver.model.HisQuotaModel;
import com.wd.cloud.wdtjserver.model.QuotaModel;

/**
 * @author He Zhigang
 * @date 2018/11/13
 * @Description:
 */
public class ModelUtil {

    public static TjDaySetting build(QuotaModel quotaModel) {
        TjDaySetting tjDaySetting = new TjDaySetting();
        tjDaySetting.setPvCount(quotaModel.getPvCount())
                .setDcCount(quotaModel.getDcCount())
                .setDdcCount(quotaModel.getDdcCount())
                .setScCount(quotaModel.getScCount())
                .setAvgTime(quotaModel.getAvgTime());
        return tjDaySetting;
    }

    public static TjHisSetting build(HisQuotaModel hisQuotaModel) {
        TjHisSetting tjHisSetting = new TjHisSetting();
        tjHisSetting.setPvCount(hisQuotaModel.getPvCount())
                .setDcCount(hisQuotaModel.getDcCount())
                .setDdcCount(hisQuotaModel.getDdcCount())
                .setScCount(hisQuotaModel.getScCount())
                .setAvgTime(hisQuotaModel.getAvgTime())
                .setBeginTime(hisQuotaModel.getBeginTime())
                .setEndTime(hisQuotaModel.getEndTime());
        return tjHisSetting;
    }
}
