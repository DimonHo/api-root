package com.wd.cloud.docdelivery.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author He Zhigang
 * @date 2018/5/22
 * @Description:
 */
@Data
@Accessors(chain = true)
@Entity
@Table(name = "audit_msg")
public class AuditMsg extends AbstractEntity {

    private String msg;
}
