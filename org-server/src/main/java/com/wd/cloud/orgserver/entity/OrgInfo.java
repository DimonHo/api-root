package com.wd.cloud.orgserver.entity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

/**
 * @author He Zhigang
 * @date 2018/8/10
 * @Description: 学校/机构信息
 */
@Entity
@Table(name = "org_info")
public class OrgInfo extends AbstractEntity {

    /**
     * 机构名称
     */
    private String name;

    /**
     * 机构默认标识
     */
    private String defaultFlag;

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

    @OneToMany(mappedBy = "org_info")
    private Set<IpRange> ipRanges;

    /**
     * 机构产品列表
     */
    @OneToMany(mappedBy = "org_info")
    private Set<OrgProduct> products;

}
