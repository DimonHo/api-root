package com.wd.cloud.uoserver.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description: 机构表
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "org")
public class Org extends AbstractEntity {

    /**
     * 机构名称
     */
    @Column(unique = true)
    private String name;

    /**
     * 机构默认标识
     */
    @Column(unique = true)
    private String flag;

    /**
     * 兼容spis机构标识
     */
    @Column(unique = true)
    private String spisFlag;

    /**
     * 兼容智汇云，学科机构标识
     */
    @Column(unique = true)
    private String eduFlag;

    /**
     * 省份
     */
    private String province;

    /**
     * 市、区
     */
    private String city;

    /**
     * 是否激活使用0：否，1：是
     */
    private boolean enabled;

}
