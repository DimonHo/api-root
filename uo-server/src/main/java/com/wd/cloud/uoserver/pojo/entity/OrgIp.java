package com.wd.cloud.uoserver.pojo.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigInteger;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description: IP范围表
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@DynamicInsert
@Table(name = "org_ip")
public class OrgIp extends AbstractEntity {

    private String orgFlag;

    private String begin;

    private String end;

    /**
     * 数据库无法保存超过20位的数字，所以这两个字段不做持久化
     */
    @Transient
    private BigInteger beginNumber;

    @Transient
    private BigInteger endNumber;

    /**
     * 是否是IPV6地址
     */
    @Column(name = "is_v6", columnDefinition = "bit(1) default 0 COMMENT '0:否，1：是'")
    private Boolean v6;
}
