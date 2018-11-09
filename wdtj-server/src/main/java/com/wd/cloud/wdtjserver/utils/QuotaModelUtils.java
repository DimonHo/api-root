package com.wd.cloud.wdtjserver.utils;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.wdtjserver.entity.TjDaySetting;
import com.wd.cloud.wdtjserver.model.QuotaModel;

import java.sql.Time;

public class QuotaModelUtils {
    private static final Log log = LogFactory.get();

    public static TjDaySetting quotaModel(Long orgId,QuotaModel quotaModel){
        TjDaySetting tjDaySetting = new TjDaySetting();
        tjDaySetting.setOrgId(orgId);
        tjDaySetting.setPvCount(quotaModel.getPvCount());
        tjDaySetting.setScCount(quotaModel.getScCount());
        tjDaySetting.setDcCount(quotaModel.getDcCount());
        tjDaySetting.setDdcCount(quotaModel.getDdcCount());
        String avgTime = quotaModel.getAvgTime();
        Time time = Time.valueOf(avgTime);
        tjDaySetting.setAvgTime(time);
        return tjDaySetting;
    }
}
