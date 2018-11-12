package com.wd.cloud.wdtjserver.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author He Zhigang
 * @date 2018/11/12
 * @Description:
 */
@Entity
@Table(name = "tj_date_setting",uniqueConstraints = {
        @UniqueConstraint(columnNames = {"date_index","date_type"})
})
public class TjDateSetting extends AbstractEntity{

    /**
     * date
     */
    private int dateIndex;

    /**
     * date类型 1:月，2：日，3：周，4：时
     */
    private int dateType;

    /**
     * 比率
     */
    private double proportion;

    public int getDateIndex() {
        return dateIndex;
    }

    public TjDateSetting setDateIndex(int dateIndex) {
        this.dateIndex = dateIndex;
        return this;
    }

    public int getDateType() {
        return dateType;
    }

    public TjDateSetting setDateType(int dateType) {
        this.dateType = dateType;
        return this;
    }

    public double getProportion() {
        return proportion;
    }

    public TjDateSetting setProportion(double proportion) {
        this.proportion = proportion;
        return this;
    }
}
