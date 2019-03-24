package com.wd.cloud.uoserver.pojo.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;

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

    private Long beginNumber;

    private Long endNumber;
}
