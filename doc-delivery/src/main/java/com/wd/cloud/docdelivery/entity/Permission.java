package com.wd.cloud.docdelivery.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author He Zhigang
 * @date 2018/12/21
 * @Description:
 */
@Entity
@Table(name = "permission")
public class Permission extends AbstractEntity {

    private Long orgId;

    private String orgName;

    /**
     * 校外：1，登陆：2，验证：4，最后相加得到权限
     */
    private Integer rule;

    /**
     * 每天求助上限
     */
    private Integer count;

    /**
     * 总求助上限
     */
    private Integer sumCount;

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

    public Integer getCount() {
        return count;
    }

    public Permission setCount(Integer count) {
        this.count = count;
        return this;
    }
}
