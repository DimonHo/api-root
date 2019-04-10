package com.wd.cloud.uoserver.pojo.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description: 机构馆藏数据库
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@DynamicInsert
@Table(name = "org_cdb",uniqueConstraints = {@UniqueConstraint(columnNames={"name", "org_flag"})})
public class OrgCdb extends AbstractEntity {

    @Column(name = "org_flag")
    private String orgFlag;

    private String name;

    private String url;
    /**
     * 资源本地地址
     */
    @Column(name = "local_url")
    private String localUrl;

    /**
     * 是否隐藏馆藏数据库
     */
    @Column(name = "is_display",columnDefinition = "bit(1) default 0 COMMENT '0:否，1：是'")
    private Boolean display;

    @Column(name = "type",columnDefinition = "tinyint(1) default 1 COMMENT '1:馆藏，2：筛选'")
    private Integer type;
}
