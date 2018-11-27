package com.wd.cloud.wdtjserver.entity;

import com.wd.cloud.wdtjserver.utils.DateUtil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.sql.Time;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description: 日基数设置表 pv >= sc & uc >= uv
 * 1. 浏览量 >= 搜索量 和 访问次数 >= 访客数量
 * 2. 平均访问时长 = 总时长/访问次数
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

    private String orgName;

    private int pvCount;

    private int scCount;

    private int dcCount;

    private int ddcCount;

    private int uvCount;

    private int ucCount;

    private Time avgTime = DateUtil.createTime(0);

    private String createUser;

    @Column(name = "pid")
    private Long pid;

    @Column(name = "is_history", columnDefinition = "bit default 0 COMMENT '是否历史记录 0：否，1：是'")
    private boolean history;

    public Long getOrgId() {
        return orgId;
    }

    public TjQuota setOrgId(Long orgId) {
        this.orgId = orgId;
        return this;
    }

    public String getOrgName() {
        return orgName;
    }

    public TjQuota setOrgName(String orgName) {
        this.orgName = orgName;
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

    public int getUvCount() {
        return uvCount;
    }

    public TjQuota setUvCount(int uvCount) {
        this.uvCount = uvCount;
        return this;
    }

    public int getUcCount() {
        return ucCount;
    }

    public TjQuota setUcCount(int ucCount) {
        this.ucCount = ucCount;
        return this;
    }
}
