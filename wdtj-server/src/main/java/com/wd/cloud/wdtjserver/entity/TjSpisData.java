package com.wd.cloud.wdtjserver.entity;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description: spis访问的真实数据
 */
@Entity
@Table(name = "tj_spis_data", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"org_id", "tj_date"})
})
public class TjSpisData extends AbstractEntity {

    /**
     * 机构ID
     */
    @Column(name = "org_id")
    private Long orgId;

    private int pvCount;
    private int scCount;
    private int dcCount;
    private int ddcCount;
    private Time avgTime;
    /**
     * 时间，精确到分钟
     */
    @Column(name = "tj_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp tjDate;

    public Long getOrgId() {
        return orgId;
    }

    public TjSpisData setOrgId(Long orgId) {
        this.orgId = orgId;
        return this;
    }

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

    public Time getAvgTime() {
        return avgTime;
    }

    public TjSpisData setAvgTime(Time avgTime) {
        this.avgTime = avgTime;
        return this;
    }

    public Timestamp getTjDate() {
        return tjDate;
    }

    public TjSpisData setTjDate(Timestamp tjDate) {
        this.tjDate = tjDate;
        return this;
    }
}
