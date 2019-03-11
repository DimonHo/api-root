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
 * @Description: 机构馆藏数据库
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "org_cdb")
public class OrgCdb extends AbstractEntity {

    @Column(name = "org_id")
    private Long orgId;
    /**
     * 馆藏数据库
     */
    @Column(name = "cdb_id")
    private Long cdbId;
    /**
     * 资源本地地址
     */
    private String localUrl;

    /**
     * 是否隐藏馆藏数据库
     */
    @Column(name = "is_display")
    private boolean display;
}
