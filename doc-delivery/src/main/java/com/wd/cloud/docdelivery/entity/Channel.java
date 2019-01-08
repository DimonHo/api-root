package com.wd.cloud.docdelivery.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author He Zhigang
 * @date 2019/1/7
 * @Description: 渠道配置表
 */
@Data
@Accessors(chain = true)
@Entity
@Table(name = "channel")
public class Channel extends AbstractEntity {

    private String name;
    private String url;
    private String template;
    /**
     * 文献过期时间
     */
    @Column(columnDefinition = "bigint(11) default 1296000000 COMMENT '全文下载过期时间'")
    private long exp;
    /**
     * 密送邮箱
     */
    private String bccs;
}
