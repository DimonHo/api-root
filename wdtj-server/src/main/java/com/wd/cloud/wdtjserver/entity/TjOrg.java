package com.wd.cloud.wdtjserver.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description: 机构设置表
 */
@Entity
@Table(name = "tj_org", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"org_id", "pid"})
})
public class TjOrg extends AbstractEntity {
    /**
     * 机构ID
     */
    @Column(name = "org_id")
    private Long orgId;
    /**
     * 机构名称
     */
    private String orgName;
    /**
     * 是否显示浏览量
     */
    private boolean showPv;

    /**
     * 是否显示访客数量
     */
    private boolean showUv;

    /**
     * 是否显示访问次数
     */
    private boolean showUc;

    /**
     * 是否显示下载量
     */
    private boolean showDc;
    /**
     * 是否显示搜索量
     */
    private boolean showSc;
    /**
     * 是否显示文献传递量
     */
    private boolean showDdc;
    /**
     * 是否显示访问量 = 总访问时长/访问次数 (avgTime = sum(visitTime)/ucCount)
     */
    private boolean showAvgTime;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 历史数据Id
     */
    private Long pid;

    @Column(name = "is_history", columnDefinition = "bit default 0 COMMENT '是否是历史记录 0：否，1：是'")
    private boolean history;

    /**
     * 是否禁用
     */
    @Column(name = "is_forbade", columnDefinition = "bit default 0 COMMENT '是否被禁用 0：否，1：是'")
    private boolean forbade;

    public Long getOrgId() {
        return orgId;
    }

    public TjOrg setOrgId(Long orgId) {
        this.orgId = orgId;
        return this;
    }

    public String getOrgName() {
        return orgName;
    }

    public TjOrg setOrgName(String orgName) {
        this.orgName = orgName;
        return this;
    }

    public boolean isShowPv() {
        return showPv;
    }

    public TjOrg setShowPv(boolean showPv) {
        this.showPv = showPv;
        return this;
    }

    public boolean isShowUv() {
        return showUv;
    }

    public TjOrg setShowUv(boolean showUv) {
        this.showUv = showUv;
        return this;
    }

    public boolean isShowUc() {
        return showUc;
    }

    public TjOrg setShowUc(boolean showUc) {
        this.showUc = showUc;
        return this;
    }

    public boolean isShowDc() {
        return showDc;
    }

    public TjOrg setShowDc(boolean showDc) {
        this.showDc = showDc;
        return this;
    }

    public boolean isShowSc() {
        return showSc;
    }

    public TjOrg setShowSc(boolean showSc) {
        this.showSc = showSc;
        return this;
    }

    public boolean isShowDdc() {
        return showDdc;
    }

    public TjOrg setShowDdc(boolean showDdc) {
        this.showDdc = showDdc;
        return this;
    }

    public boolean isShowAvgTime() {
        return showAvgTime;
    }

    public TjOrg setShowAvgTime(boolean showAvgTime) {
        this.showAvgTime = showAvgTime;
        return this;
    }

    public String getCreateUser() {
        return createUser;
    }

    public TjOrg setCreateUser(String createUser) {
        this.createUser = createUser;
        return this;
    }

    public Long getPid() {
        return pid;
    }

    public TjOrg setPid(Long pid) {
        this.pid = pid;
        return this;
    }

    public boolean isHistory() {
        return history;
    }

    public TjOrg setHistory(boolean history) {
        this.history = history;
        return this;
    }

    public boolean isForbade() {
        return forbade;
    }

    public TjOrg setForbade(boolean forbade) {
        this.forbade = forbade;
        return this;
    }
}