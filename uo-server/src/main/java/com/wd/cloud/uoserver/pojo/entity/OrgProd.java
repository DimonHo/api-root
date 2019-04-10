package com.wd.cloud.uoserver.pojo.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Date;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description: 机构产品表
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@DynamicInsert
@Table(name = "org_prod",uniqueConstraints = {@UniqueConstraint(columnNames={"org_flag", "prod_id"})})
public class OrgProd extends AbstractEntity {

    @Column(name = "org_flag")
    private String orgFlag;

    /**
     * 产品
     */
    @Column(name = "prod_id")
    private Long prodId;

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
    private Boolean single;
}
