package com.wd.cloud.orgserver.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author He Zhigang
 * @date 2018/8/10
 * @Description: IP范围
 */
@Entity
@Data
@Accessors(chain = true)
@Table(name = "ip_range")
public class IpRange extends AbstractEntity {

    private Long orgId;

    private String orgName;

    private String begin;

    private String end;
}
