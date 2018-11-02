package com.wd.cloud.wdtjserver.entity;

/**
 * @author He Zhigang
 * @date 2018/11/1
 * @Description:
 */
public class PvIndex {
    /**
     * 产品id
     */
    private int product;
    /**
     * 来源渠道
     */
    private String channel;
    /**
     * 浏览量=访问总页面数/访问人次
     */
    private int pvCount;
    /**
     * 独立访客数
     */
    private int uvCount;

    /**
     * 访问次数
     */
    private int vvCount;
    /**
     * IP数
     */
    private int ipCount;
    /**
     * 平均访问时长
     */
    private long avgDate;

    /**
     * 平均访问页数
     */
    private double avgPage;
    /**
     * 检索量
     */
    private int searchCount;
}
