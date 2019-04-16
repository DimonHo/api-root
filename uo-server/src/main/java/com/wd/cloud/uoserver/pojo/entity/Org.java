package com.wd.cloud.uoserver.pojo.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;

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
@DynamicInsert
@Entity
@Table(name = "org")
public class Org extends AbstractEntity {

    @Column(unique = true)
    private String flag;

    /**
     * 机构名称
     */
    @Column(unique = true)
    private String name;

    /**
     * 省份
     */
    private String province;

    /**
     * 市、区
     */
    private String city;

    /**
     * 是否停止使用0：否，1：是
     */
    @Column(name = "is_disable", columnDefinition = "bit(1) default 0")
    private Boolean disable;


}
