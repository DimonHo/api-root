package com.wd.cloud.uoserver.pojo.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;


/**
 * @author He Zhigang
 * @date 2019/1/15
 * @Description: 审核日志
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "audit_log")
public class AuditLog extends AbstractEntity {

    private String username;

    /**
     * 证件照
     */
    @Column(name = "id_photo")
    private String idPhoto;

    /**
     * 状态 审核不通过,待审核，审核通过
     */
    @Column(name = "status",columnDefinition = "tinyint(1) default 0 COMMENT '0:认证不通过，1：待认证，2：认证通过'")
    private Integer status;

    /**
     * 操作人
     */
    @Column(name = "handler_name")
    private String handlerName;

    /**
     * 失败原因
     */
    private String remark;

}
