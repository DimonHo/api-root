package com.wd.cloud.uoserver.pojo.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;
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
@Table(name = "org_ip")
public class OrgIp extends AbstractEntity {

    private String orgFlag;

    private String begin;

    private String end;

    private BigInteger beginNumber;

    private BigInteger endNumber;

    /**
     * 是否是IPV6地址
     */
    @Column(name = "is_v6", columnDefinition = "bit(1) default 0 COMMENT '0:否，1：是'")
    private Boolean v6;
}
