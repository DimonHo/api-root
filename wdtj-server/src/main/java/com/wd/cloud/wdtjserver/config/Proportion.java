package com.wd.cloud.wdtjserver.config;

/**
 * @author He Zhigang
 * @date 2018/11/8
 * @Description:
 */
public class Proportion {
    /**
     * 区间
     */
    private int[] options;
    /**
     * 比率
     */
    private double proportion;

    public int[] getOptions() {
        return options;
    }

    public Proportion setOptions(int[] options) {
        this.options = options;
        return this;
    }

    public double getProportion() {
        return proportion;
    }

    public Proportion setProportion(double proportion) {
        this.proportion = proportion;
        return this;
    }
}
