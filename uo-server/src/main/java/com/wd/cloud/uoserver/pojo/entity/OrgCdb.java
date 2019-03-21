package com.wd.cloud.uoserver.pojo.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description: 机构馆藏数据库
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "org_cdb")
public class OrgCdb extends AbstractEntity {

    @Column(name = "org_flag")
    private String orgFlag;
    /**
     * 馆藏数据库
     */
    @Column(name = "cdb_id")
    private Long cdbId;
    /**
     * 资源本地地址
     */
    @Column(name = "local_url")
    private String localUrl;

    /**
     * 是否隐藏馆藏数据库
     */
    @Column(name = "is_display",columnDefinition = "tinyint(1) default 1 COMMENT '0:是，1：否'")
    private boolean display;

    @Column(name = "is_collection",columnDefinition = "tinyint(1) default 1 COMMENT '0:数据库筛选，1：馆藏数据库'")
    private boolean collection;
}
