package com.wd.cloud.wdtjserver.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author He Zhigang
 * @date 2018/11/12
 * @Description: 权重表
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
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
     * date类型 1:月，2：周，3：日，4：时
     */
    @Column(name = "date_type")
    private int dateType;

    /**
     * 备注
     */
    private String remark;

    /**
     * 最低权重
     */
    private Double low;

    /**
     * 最高权重
     */
    private Double high;
}
