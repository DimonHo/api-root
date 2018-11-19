package com.wd.cloud.wdtjserver.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Time;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description: 展示给用户的最终数据
 */
@Entity
@Table(name = "tj_view_data")
public class TjViewData extends AbstractTjDataEntity {

    private int pvCount;
    private int scCount;
    private int dcCount;
    private int ddcCount;

    private Time visitTime = new Time(0);


    public int getPvCount() {
        return pvCount;
    }

    public TjViewData setPvCount(int pvCount) {
        this.pvCount = pvCount;
        return this;
    }

    public int getScCount() {
        return scCount;
    }

    public TjViewData setScCount(int scCount) {
        this.scCount = scCount;
        return this;
    }

    public int getDcCount() {
        return dcCount;
    }

    public TjViewData setDcCount(int dcCount) {
        this.dcCount = dcCount;
        return this;
    }

    public int getDdcCount() {
        return ddcCount;
    }

    public TjViewData setDdcCount(int ddcCount) {
        this.ddcCount = ddcCount;
        return this;
    }

    public Time getVisitTime() {
        return visitTime;
    }

    public TjViewData setVisitTime(Time visitTime) {
        this.visitTime = visitTime;
        return this;
    }

}
