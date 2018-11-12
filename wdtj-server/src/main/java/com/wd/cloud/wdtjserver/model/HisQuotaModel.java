package com.wd.cloud.wdtjserver.model;

import java.sql.Timestamp;

/**
 * @author He Zhigang
 * @date 2018/11/8 0008
 * @Description:
 */
public class HisQuotaModel extends QuotaModel{

    private Timestamp beginTime;
    private Timestamp endTime;

    public Timestamp getBeginTime() {
        return beginTime;
    }

    public HisQuotaModel setBeginTime(Timestamp beginTime) {
        this.beginTime = beginTime;
        return this;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public HisQuotaModel setEndTime(Timestamp endTime) {
        this.endTime = endTime;
        return this;
    }
}
