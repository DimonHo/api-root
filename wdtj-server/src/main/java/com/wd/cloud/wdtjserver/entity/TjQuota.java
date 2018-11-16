package com.wd.cloud.wdtjserver.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.sql.Time;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description: 日基数设置表
 */
@Entity
@Table(name = "tj_quota", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"org_id", "pid"})
})
public class TjQuota extends AbstractEntity {

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

    private String createUser;
    @Column(name = "pid")
    private Long pid;
    private boolean history;

    public Long getOrgId() {
        return orgId;
    }

    public TjQuota setOrgId(Long orgId) {
        this.orgId = orgId;
        return this;
    }

    public int getPvCount() {
        return pvCount;
    }

    public TjQuota setPvCount(int pvCount) {
        this.pvCount = pvCount;
        return this;
    }

    public int getScCount() {
        return scCount;
    }

    public TjQuota setScCount(int scCount) {
        this.scCount = scCount;
        return this;
    }

    public int getDcCount() {
        return dcCount;
    }

    public TjQuota setDcCount(int dcCount) {
        this.dcCount = dcCount;
        return this;
    }

    public int getDdcCount() {
        return ddcCount;
    }

    public TjQuota setDdcCount(int ddcCount) {
        this.ddcCount = ddcCount;
        return this;
    }

    public Time getAvgTime() {
        return avgTime;
    }

    public TjQuota setAvgTime(Time avgTime) {
        this.avgTime = avgTime;
        return this;
    }

    public String getCreateUser() {
        return createUser;
    }

    public TjQuota setCreateUser(String createUser) {
        this.createUser = createUser;
        return this;
    }


    public Long getPid() {
        return pid;
    }

    public TjQuota setPid(Long pid) {
        this.pid = pid;
        return this;
    }

    public boolean isHistory() {
        return history;
    }

    public TjQuota setHistory(boolean history) {
        this.history = history;
        return this;
    }
}
