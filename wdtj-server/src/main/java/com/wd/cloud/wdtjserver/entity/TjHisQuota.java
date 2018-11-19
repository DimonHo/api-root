package com.wd.cloud.wdtjserver.entity;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Time;
import java.util.Date;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
@Entity
@Table(name = "tj_his_quota")
public class TjHisQuota extends AbstractEntity {

    /**
     * 机构ID
     */
    @Column(name = "org_id")
    private Long orgId;

    private int pvCount;

    private int scCount;

    private int dcCount;

    private int ddcCount;

    private int uvCount;

    private int ucCount;

    private long avgTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date beginTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    private String createUser;
    /**
     * 是否上锁，如果已上锁，则不可覆盖
     */
    @Column(name = "is_locked", columnDefinition = "bit default 0 COMMENT '是否已经被锁定无法修改'")
    private boolean locked;

    /**
     * 是否是历史的记录
     */
    @Column(name = "is_history", columnDefinition = "bit default 0 COMMENT '是否是历史记录（作废），0:否，1：是'")
    private boolean history;

    /**
     * 是否已生成
     */
    @Column(name = "is_built", columnDefinition = "bit default 0 COMMENT '是否已经生成过数据，0：否，1：是'")
    private boolean built;

    public Long getOrgId() {
        return orgId;
    }

    public TjHisQuota setOrgId(Long orgId) {
        this.orgId = orgId;
        return this;
    }

    public int getPvCount() {
        return pvCount;
    }

    public TjHisQuota setPvCount(int pvCount) {
        this.pvCount = pvCount;
        return this;
    }

    public int getScCount() {
        return scCount;
    }

    public TjHisQuota setScCount(int scCount) {
        this.scCount = scCount;
        return this;
    }

    public int getDcCount() {
        return dcCount;
    }

    public TjHisQuota setDcCount(int dcCount) {
        this.dcCount = dcCount;
        return this;
    }

    public int getDdcCount() {
        return ddcCount;
    }

    public TjHisQuota setDdcCount(int ddcCount) {
        this.ddcCount = ddcCount;
        return this;
    }

    public int getUvCount() {
        return uvCount;
    }

    public TjHisQuota setUvCount(int uvCount) {
        this.uvCount = uvCount;
        return this;
    }

    public int getUcCount() {
        return ucCount;
    }

    public TjHisQuota setUcCount(int ucCount) {
        this.ucCount = ucCount;
        return this;
    }

    public long getAvgTime() {
        return avgTime;
    }

    public TjHisQuota setAvgTime(long avgTime) {
        this.avgTime = avgTime;
        return this;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public TjHisQuota setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
        return this;
    }

    public Date getEndTime() {
        return endTime;
    }

    public TjHisQuota setEndTime(Date endTime) {
        this.endTime = endTime;
        return this;
    }

    public String getCreateUser() {
        return createUser;
    }

    public TjHisQuota setCreateUser(String createUser) {
        this.createUser = createUser;
        return this;
    }

    public boolean isLocked() {
        return locked;
    }

    public TjHisQuota setLocked(boolean locked) {
        this.locked = locked;
        return this;
    }

    public boolean isHistory() {
        return history;
    }

    public TjHisQuota setHistory(boolean history) {
        this.history = history;
        return this;
    }

    public boolean isBuilt() {
        return built;
    }

    public TjHisQuota setBuilt(boolean built) {
        this.built = built;
        return this;
    }
}
