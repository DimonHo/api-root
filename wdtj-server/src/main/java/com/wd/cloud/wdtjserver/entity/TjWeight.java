package com.wd.cloud.wdtjserver.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author He Zhigang
 * @date 2018/11/12
 * @Description: 权重表
 */
@Entity
@Table(name = "tj_weight", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"date_index", "date_type"})
})
public class TjWeight extends AbstractEntity {

    /**
     * date:1, 15, 6
     */
    @Column(name = "date_index")
    private int dateIndex;

    /**
     * date类型 1:月，2：日，3：周，4：时
     */
    @Column(name = "date_type")
    private int dateType;

    /**
     * 备注
     */
    private String remark;

    /**
     * 权重
     */
    private Double weight;

    public int getDateIndex() {
        return dateIndex;
    }

    public TjWeight setDateIndex(int dateIndex) {
        this.dateIndex = dateIndex;
        return this;
    }

    public int getDateType() {
        return dateType;
    }

    public TjWeight setDateType(int dateType) {
        this.dateType = dateType;
        return this;
    }

    public Double getWeight() {
        return weight;
    }

    public TjWeight setWeight(Double weight) {
        this.weight = weight;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public TjWeight setRemark(String remark) {
        this.remark = remark;
        return this;
    }
}