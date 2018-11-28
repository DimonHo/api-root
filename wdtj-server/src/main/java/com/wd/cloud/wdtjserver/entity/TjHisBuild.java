package com.wd.cloud.wdtjserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author He Zhigang
 * @date 2018/11/28
 * @Description: 历史记录生成记录
 */
@Entity
@Table(name = "tj_his_build")
public class TjHisBuild extends AbstractEntity {

    private String name;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "tj_his_quota_id")
    private TjHisQuota tjHisQuota;

    public String getName() {
        return name;
    }

    public TjHisBuild setName(String name) {
        this.name = name;
        return this;
    }

    public TjHisQuota getTjHisQuota() {
        return tjHisQuota;
    }

    public TjHisBuild setTjHisQuota(TjHisQuota tjHisQuota) {
        this.tjHisQuota = tjHisQuota;
        return this;
    }
}
