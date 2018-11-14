package com.wd.cloud.orgserver.entity;

import javax.persistence.*;
import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description: 机构参数设置
 */
@Entity
@Table(name = "org_setting")
public class OrgSetting extends AbstractEntity {

    @OneToOne
    private Org org;

    /**
     * 是否默认检索开放资源
     */
    private boolean defaultOpen;

    /**
     * 中科院数据[2012,2013,2014,...]
     */
    private String zkyData;

    /**
     * 登陆用户文献传递数量
     */
    private int loginDdc;

    /**
     * 游客文献传递数量
     */
    private int touristDdc;

    /**
     * 过滤数据库列表
     */
    @ManyToMany
    @JoinTable(name = "org_setting_collection",
            joinColumns = {@JoinColumn(name = "collection_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "org_setting_id", referencedColumnName = "id")})
    private List<Collection> filterDbs;

    public Org getOrg() {
        return org;
    }

    public OrgSetting setOrg(Org org) {
        this.org = org;
        return this;
    }

    public boolean isDefaultOpen() {
        return defaultOpen;
    }

    public OrgSetting setDefaultOpen(boolean defaultOpen) {
        this.defaultOpen = defaultOpen;
        return this;
    }

    public String getZkyData() {
        return zkyData;
    }

    public OrgSetting setZkyData(String zkyData) {
        this.zkyData = zkyData;
        return this;
    }

    public int getLoginDdc() {
        return loginDdc;
    }

    public OrgSetting setLoginDdc(int loginDdc) {
        this.loginDdc = loginDdc;
        return this;
    }

    public int getTouristDdc() {
        return touristDdc;
    }

    public OrgSetting setTouristDdc(int touristDdc) {
        this.touristDdc = touristDdc;
        return this;
    }

    public List<Collection> getFilterDbs() {
        return filterDbs;
    }

    public OrgSetting setFilterDbs(List<Collection> filterDbs) {
        this.filterDbs = filterDbs;
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

        OrgSetting that = (OrgSetting) o;

        if (defaultOpen != that.defaultOpen) {
            return false;
        }
        if (loginDdc != that.loginDdc) {
            return false;
        }
        if (touristDdc != that.touristDdc) {
            return false;
        }
        if (org != null ? !org.equals(that.org) : that.org != null) {
            return false;
        }
        if (zkyData != null ? !zkyData.equals(that.zkyData) : that.zkyData != null) {
            return false;
        }
        return filterDbs != null ? filterDbs.equals(that.filterDbs) : that.filterDbs == null;
    }

    @Override
    public int hashCode() {
        int result = org != null ? org.hashCode() : 0;
        result = 31 * result + (defaultOpen ? 1 : 0);
        result = 31 * result + (zkyData != null ? zkyData.hashCode() : 0);
        result = 31 * result + loginDdc;
        result = 31 * result + touristDdc;
        result = 31 * result + (filterDbs != null ? filterDbs.hashCode() : 0);
        return result;
    }
}
