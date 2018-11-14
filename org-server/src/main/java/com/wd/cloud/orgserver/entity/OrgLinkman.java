package com.wd.cloud.orgserver.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description: 机构联系人信息
 */
@Entity
@Table(name = "org_linkman")
public class OrgLinkman extends AbstractEntity {

    /**
     * 联系人名称
     */
    private String name;
    /**
     * 联系人邮箱
     */
    private String email;
    /**
     * 联系人电话
     */
    private String phone;

    @ManyToOne
    @JoinColumn(name = "org_id")
    private Org org;


    public String getName() {
        return name;
    }

    public OrgLinkman setName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public OrgLinkman setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public OrgLinkman setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public Org getOrg() {
        return org;
    }

    public OrgLinkman setOrg(Org org) {
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

        OrgLinkman that = (OrgLinkman) o;

        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (email != null ? !email.equals(that.email) : that.email != null) {
            return false;
        }
        if (phone != null ? !phone.equals(that.phone) : that.phone != null) {
            return false;
        }
        return org != null ? org.equals(that.org) : that.org == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (org != null ? org.hashCode() : 0);
        return result;
    }
}
