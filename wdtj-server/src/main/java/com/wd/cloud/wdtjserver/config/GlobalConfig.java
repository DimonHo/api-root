package com.wd.cloud.wdtjserver.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author He Zhigang
 * @date 2018/11/8
 * @Description: 高峰/低谷比率配置
 */
@Component
@ConfigurationProperties(value = "global.tj")
public class GlobalConfig {

    /**
     * 高峰时段
     */
    private Proportion highHours;

    private Proportion lowHours;

    /**
     * 高峰日期
     */
    private Proportion highDays;

    private Proportion lowDays;

    /**
     * 高峰月份
     */
    private Proportion highMonths;

    private Proportion lowMonths;

    public Proportion getHighHours() {
        return highHours;
    }

    public GlobalConfig setHighHours(Proportion highHours) {
        this.highHours = highHours;
        return this;
    }

    public Proportion getLowHours() {
        return lowHours;
    }

    public GlobalConfig setLowHours(Proportion lowHours) {
        this.lowHours = lowHours;
        return this;
    }

    public Proportion getHighDays() {
        return highDays;
    }

    public GlobalConfig setHighDays(Proportion highDays) {
        this.highDays = highDays;
        return this;
    }

    public Proportion getLowDays() {
        return lowDays;
    }

    public GlobalConfig setLowDays(Proportion lowDays) {
        this.lowDays = lowDays;
        return this;
    }

    public Proportion getHighMonths() {
        return highMonths;
    }

    public GlobalConfig setHighMonths(Proportion highMonths) {
        this.highMonths = highMonths;
        return this;
    }

    public Proportion getLowMonths() {
        return lowMonths;
    }

    public GlobalConfig setLowMonths(Proportion lowMonths) {
        this.lowMonths = lowMonths;
        return this;
    }
}
