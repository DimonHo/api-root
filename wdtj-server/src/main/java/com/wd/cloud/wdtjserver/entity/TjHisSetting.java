package com.wd.cloud.wdtjserver.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.sql.Date;
import java.sql.Time;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
@Entity
@Table(name = "tj_his_setting", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"org_id","pid"})
})
public class TjHisSetting extends AbstractEntity {

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

    private Date beginDate;
    private Date endDate;
    private String createUser;
    /**
     * 是否上锁，如果已上锁，则不可覆盖
     */
    private boolean locked;
    private boolean history;

    public Long getOrgId() {
        return orgId;
    }

    public TjHisSetting setOrgId(Long orgId) {
        this.orgId = orgId;
        return this;
    }

    public int getPvCount() {
        return pvCount;
    }

    public TjHisSetting setPvCount(int pvCount) {
        this.pvCount = pvCount;
        return this;
    }

    public int getScCount() {
        return scCount;
    }

    public TjHisSetting setScCount(int scCount) {
        this.scCount = scCount;
        return this;
    }

    public int getDcCount() {
        return dcCount;
    }

    public TjHisSetting setDcCount(int dcCount) {
        this.dcCount = dcCount;
        return this;
    }

    public int getDdcCount() {
        return ddcCount;
    }

    public TjHisSetting setDdcCount(int ddcCount) {
        this.ddcCount = ddcCount;
        return this;
    }

    public Time getAvgTime() {
        return avgTime;
    }

    public TjHisSetting setAvgTime(Time avgTime) {
        this.avgTime = avgTime;
        return this;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public TjHisSetting setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
        return this;
    }

    public Date getEndDate() {
        return endDate;
    }

    public TjHisSetting setEndDate(Date endDate) {
        this.endDate = endDate;
        return this;
    }

    public String getCreateUser() {
        return createUser;
    }

    public TjHisSetting setCreateUser(String createUser) {
        this.createUser = createUser;
        return this;
    }

    public boolean isLocked() {
        return locked;
    }

    public TjHisSetting setLocked(boolean locked) {
        this.locked = locked;
        return this;
    }

    public boolean isHistory() {
        return history;
    }

    public TjHisSetting setHistory(boolean history) {
        this.history = history;
        return this;
    }
}
