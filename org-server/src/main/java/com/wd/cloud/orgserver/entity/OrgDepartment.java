package com.wd.cloud.orgserver.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description: 院系
 */
@Entity
@Table(name = "org_department")
public class OrgDepartment extends AbstractEntity{

    /**
     * 所属上级院系
     */
    private Long pid;

    /**
     * 院系名称
     */
    private String name;

    @ManyToOne
    @JoinColumn(name = "org_id")
    private Org org;

    public Long getPid() {
        return pid;
    }

    public OrgDepartment setPid(Long pid) {
        this.pid = pid;
        return this;
    }

    public String getName() {
        return name;
    }

    public OrgDepartment setName(String name) {
        this.name = name;
        return this;
    }

    public Org getOrg() {
        return org;
    }

    public OrgDepartment setOrg(Org org) {
        this.org = org;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OrgDepartment that = (OrgDepartment) o;

        if (pid != null ? !pid.equals(that.pid) : that.pid != null) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        return org != null ? org.equals(that.org) : that.org == null;
    }

    @Override
    public int hashCode() {
        int result = pid != null ? pid.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (org != null ? org.hashCode() : 0);
        return result;
    }
}
