package com.wd.cloud.uoserver.pojo.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;

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
    @Column(name = "is_disable",columnDefinition = "bit(1) default 0")
    private boolean disable;


}
