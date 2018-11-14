package com.wd.cloud.orgserver.entity;

import javax.persistence.*;
import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/8/10
 * @Description: 学校/机构信息
 */
@Entity
@Table(name = "org")
public class Org extends AbstractEntity {

    /**
     * 机构名称
     */
    private String name;

    /**
     * 机构默认标识
     */
    private String flag;

    /**
     * 兼容spis机构标识
     */
    private String spisFlag;

    /**
     * 兼容智汇云，学科机构标识
     */
    private String eduFlag;

    /**
     * 省份
     */
    private String province;

    /**
     * 市、区
     */
    private String city;

    @OrderBy("begin ASC, end DESC")
    @OneToMany(mappedBy = "org")
    private List<IpRange> ipRanges;

    @OneToMany(mappedBy = "org")
    private List<OrgLinkman> orgLinkmans;

    @OneToMany(mappedBy = "org")
    private List<OrgDepartment> orgDepartments;

    @OneToOne(mappedBy = "org")
    private OrgSetting orgSetting;
    /**
     * 机构产品列表
     */
    @OneToMany(mappedBy = "org")
    private List<OrgProduct> orgProducts;

    @OneToMany(mappedBy = "org")
    private List<OrgCollection> orgCollections;

    public String getName() {
        return name;
    }

    public Org setName(String name) {
        this.name = name;
        return this;
    }

    public String getFlag() {
        return flag;
    }

    public Org setFlag(String flag) {
        this.flag = flag;
        return this;
    }

    public String getSpisFlag() {
        return spisFlag;
    }

    public Org setSpisFlag(String spisFlag) {
        this.spisFlag = spisFlag;
        return this;
    }

    public String getEduFlag() {
        return eduFlag;
    }

    public Org setEduFlag(String eduFlag) {
        this.eduFlag = eduFlag;
        return this;
    }

    public String getProvince() {
        return province;
    }

    public Org setProvince(String province) {
        this.province = province;
        return this;
    }

    public String getCity() {
        return city;
    }

    public Org setCity(String city) {
        this.city = city;
        return this;
    }

    public List<IpRange> getIpRanges() {
        return ipRanges;
    }

    public Org setIpRanges(List<IpRange> ipRanges) {
        this.ipRanges = ipRanges;
        return this;
    }

    public List<OrgLinkman> getOrgLinkmans() {
        return orgLinkmans;
    }

    public Org setOrgLinkmans(List<OrgLinkman> orgLinkmans) {
        this.orgLinkmans = orgLinkmans;
        return this;
    }

    public List<OrgDepartment> getOrgDepartments() {
        return orgDepartments;
    }

    public Org setOrgDepartments(List<OrgDepartment> orgDepartments) {
        this.orgDepartments = orgDepartments;
        return this;
    }

    public OrgSetting getOrgSetting() {
        return orgSetting;
    }

    public Org setOrgSetting(OrgSetting orgSetting) {
        this.orgSetting = orgSetting;
        return this;
    }

    public List<OrgProduct> getOrgProducts() {
        return orgProducts;
    }

    public Org setOrgProducts(List<OrgProduct> orgProducts) {
        this.orgProducts = orgProducts;
        return this;
    }

    public List<OrgCollection> getOrgCollections() {
        return orgCollections;
    }

    public Org setOrgCollections(List<OrgCollection> orgCollections) {
        this.orgCollections = orgCollections;
        return this;
    }
}
