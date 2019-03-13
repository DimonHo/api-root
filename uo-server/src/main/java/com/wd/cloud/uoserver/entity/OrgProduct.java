package com.wd.cloud.uoserver.entity;

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
@Table(name = "org_product")
public class OrgProduct extends AbstractEntity {

    @Column(name = "org_id")
    private Long orgId;

    /**
     * 产品
     */
    @Column(name = "product_id")
    private Long productId;

    /**
     * 开始时间
     */
    private Date beginDate;

    /**
     * 过期时间
     */
    private Date endDate;

    /**
     * 状态  1购买、2试用、0停用
     */
    private Integer status;
}
