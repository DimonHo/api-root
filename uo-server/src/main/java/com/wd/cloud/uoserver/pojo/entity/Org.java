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
    /**
     * 机构默认标识
     */
    @Id
    @GeneratedValue
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
     * 是否激活使用0：否，1：是
     */
    @Column(name = "is_enabled",columnDefinition = "bit(1) default 1")
    private boolean enabled;


}
