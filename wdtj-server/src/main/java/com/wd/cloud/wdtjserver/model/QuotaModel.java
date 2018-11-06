package com.wd.cloud.wdtjserver.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
@ApiModel(value = "统计指标对象")
public class QuotaModel {

    @ApiModelProperty(value = "访问量", example = "10")
    private int pvCount;
    @ApiModelProperty(value = "搜索量", example = "10")
    private int scCount;
    @ApiModelProperty(value = "下载量", example = "10")
    private int dcCount;
    @ApiModelProperty(value = "文献传递量", example = "10")
    private int ddcCount;
    @ApiModelProperty(value = "平均访问时长", example = "00:05:21")
    private String avgTime;

    public int getPvCount() {
        return pvCount;
    }

    public QuotaModel setPvCount(int pvCount) {
        this.pvCount = pvCount;
        return this;
    }

    public int getScCount() {
        return scCount;
    }

    public QuotaModel setScCount(int scCount) {
        this.scCount = scCount;
        return this;
    }

    public int getDcCount() {
        return dcCount;
    }

    public QuotaModel setDcCount(int dcCount) {
        this.dcCount = dcCount;
        return this;
    }

    public int getDdcCount() {
        return ddcCount;
    }

    public QuotaModel setDdcCount(int ddcCount) {
        this.ddcCount = ddcCount;
        return this;
    }

    public String getAvgTime() {
        return avgTime;
    }

    public QuotaModel setAvgTime(String avgTime) {
        this.avgTime = avgTime;
        return this;
    }
}
