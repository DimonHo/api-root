package com.wd.cloud.uoserver.pojo.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.sql.Date;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description: 机构产品表
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "org_product",uniqueConstraints = {@UniqueConstraint(columnNames={"org_flag", "product_id"})})
public class OrgProduct extends AbstractEntity {

    @Column(name = "org_flag")
    private String orgFlag;

    /**
     * 产品
     */
    @Column(name = "product_id")
    private Long productId;

    /**
     * 开始时间
     */
    private Date effDate;

    /**
     * 过期时间
     */
    private Date expDate;

    /**
     * 状态  1购买、2试用、0停用
     */
    private Integer status;


    /**
     * 是否独立购买
     */
    @Column(name = "is_single",columnDefinition = "tinyint(1) default 0 COMMENT '0:否，1：是'")
    private boolean single;
}
