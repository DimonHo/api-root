package com.wd.cloud.wdtjserver.model;

import cn.hutool.core.date.DateTime;

/**
 * @author He Zhigang
 * @date 2018/11/15
 * @Description: 权重模型
 */
public class WeightModel implements Comparable {

    private String name;

    private double value;

    public String getName() {
        return name;
    }

    public WeightModel setName(String name) {
        this.name = name;
        return this;
    }

    public double getValue() {
        return value;
    }

    public WeightModel setValue(double value) {
        this.value = value;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        WeightModel that = (WeightModel) o;

        if (Double.compare(that.value, value) != 0) {
            return false;
        }
        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = name != null ? name.hashCode() : 0;
        temp = Double.doubleToLongBits(value);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public int compareTo(Object o) {
        WeightModel that = (WeightModel) o;
        if (this.equals(o)) {
            return 0;
        } else if (this.value > that.value) {
            return -1;
        } else {
            return 1;
        }
    }
}
