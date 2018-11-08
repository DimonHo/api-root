package com.wd.cloud.orgserver.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.Set;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description: 机构参数设置
 */
@Entity
public class OrgSetting extends AbstractEntity {

    @OneToOne
    private OrgInfo orgInfo;

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
    @OneToMany
    private Set<CollectionDB> filterDBS;


    public OrgInfo getOrgInfo() {
        return orgInfo;
    }

    public OrgSetting setOrgInfo(OrgInfo orgInfo) {
        this.orgInfo = orgInfo;
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
}
