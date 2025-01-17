package com.wd.cloud.wdtjserver.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author He Zhigang
 * @date 2018/11/28
 * @Description: 历史记录生成记录
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tj_his_build")
public class TjHisBuild extends AbstractEntity {

    private String name;

    @Column(name = "tj_his_quota_id")
    private Long tjHisQuotaId;

}
