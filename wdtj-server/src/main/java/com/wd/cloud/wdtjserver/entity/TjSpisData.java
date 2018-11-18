package com.wd.cloud.wdtjserver.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Time;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description: spis访问的真实数据
 */
@Entity
@Table(name = "tj_spis_data")
public class TjSpisData extends AbstractTjDataEntity{

    private int pvCount;
    private int scCount;
    private int dcCount;
    private int ddcCount;
    private Time visitTime = new Time(0);

    public int getPvCount() {
        return pvCount;
    }

    public TjSpisData setPvCount(int pvCount) {
        this.pvCount = pvCount;
        return this;
    }

    public int getScCount() {
        return scCount;
    }

    public TjSpisData setScCount(int scCount) {
        this.scCount = scCount;
        return this;
    }

    public int getDcCount() {
        return dcCount;
    }

    public TjSpisData setDcCount(int dcCount) {
        this.dcCount = dcCount;
        return this;
    }

    public int getDdcCount() {
        return ddcCount;
    }

    public TjSpisData setDdcCount(int ddcCount) {
        this.ddcCount = ddcCount;
        return this;
    }

    public Time getVisitTime() {
        return visitTime;
    }

    public TjSpisData setVisitTime(Time visitTime) {
        this.visitTime = visitTime;
        return this;
    }

}
