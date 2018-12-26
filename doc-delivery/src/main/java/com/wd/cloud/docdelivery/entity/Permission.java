package com.wd.cloud.docdelivery.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author He Zhigang
 * @date 2018/12/21
 * @Description:
 */
@Entity
@Table(name = "permission", uniqueConstraints = {@UniqueConstraint(columnNames = {"org_id", "rule"})})
public class Permission extends AbstractEntity {

    @Column(name = "org_id")
    private Long orgId;

    private String orgName;

    /**
     * 校外：1，登陆：2，验证：4，最后相加得到权限
     */
    @Column(name = "rule")
    private Integer rule;

    /**
     * 每天求助上限 ，-1则表示无上限
     */
    private Long todayTotal;

    /**
     * 总求助上限，-1则表示无上限
     */
    private Long total;

    public Long getOrgId() {
        return orgId;
    }

    public Permission setOrgId(Long orgId) {
        this.orgId = orgId;
        return this;
    }

    public String getOrgName() {
        return orgName;
    }

    public Permission setOrgName(String orgName) {
        this.orgName = orgName;
        return this;
    }

    public Integer getRule() {
        return rule;
    }

    public Permission setRule(Integer rule) {
        this.rule = rule;
        return this;
    }

    public Long getTodayTotal() {
        return todayTotal;
    }

    public Permission setTodayTotal(Long todayTotal) {
        this.todayTotal = todayTotal;
        return this;
    }

    public Long getTotal() {
        return total;
    }

    public Permission setTotal(Long total) {
        this.total = total;
        return this;
    }
}
