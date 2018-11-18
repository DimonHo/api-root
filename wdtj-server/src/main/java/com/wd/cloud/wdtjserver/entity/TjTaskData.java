package com.wd.cloud.wdtjserver.entity;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description: 根据日基数算法每天生成数据
 */
@Entity
@Table(name = "tj_task_data")
public class TjTaskData extends AbstractTjDataEntity {


    private int pvCount;
    private int scCount;
    private int dcCount;
    private int ddcCount;
    private Time visitTime = new Time(0);

    public int getPvCount() {
        return pvCount;
    }

    public TjTaskData setPvCount(int pvCount) {
        this.pvCount = pvCount;
        return this;
    }

    public int getScCount() {
        return scCount;
    }

    public TjTaskData setScCount(int scCount) {
        this.scCount = scCount;
        return this;
    }

    public int getDcCount() {
        return dcCount;
    }

    public TjTaskData setDcCount(int dcCount) {
        this.dcCount = dcCount;
        return this;
    }

    public int getDdcCount() {
        return ddcCount;
    }

    public TjTaskData setDdcCount(int ddcCount) {
        this.ddcCount = ddcCount;
        return this;
    }

    public Time getVisitTime() {
        return visitTime;
    }

    public TjTaskData setVisitTime(Time visitTime) {
        this.visitTime = visitTime;
        return this;
    }

}
