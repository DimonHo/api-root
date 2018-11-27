package com.wd.cloud.wdtjserver.model;

import com.wd.cloud.wdtjserver.utils.DateUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Min;
import java.sql.Time;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description: 统计指标模型
 */

@ApiModel(value = "统计指标模型")
public class QuotaModel {

    @Min(0)
    @ApiModelProperty(value = "浏览量", example = "10")
    private int pvCount;

    @Min(0)
    @ApiModelProperty(value = "搜索量", example = "10")
    private int scCount;

    @Min(0)
    @ApiModelProperty(value = "下载量", example = "10")
    private int dcCount;

    @Min(0)
    @ApiModelProperty(value = "文献传递量", example = "10")
    private int ddcCount;

    @ApiModelProperty(value = "平均访问时长", example = "00:00:00")
    private Time avgTime = DateUtil.createTime(0);

    @Min(0)
    @ApiModelProperty(value = "访客数量", example = "10")
    private int uvCount;

    @Min(0)
    @ApiModelProperty(value = "访问次数", example = "10")
    private int ucCount;

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

    public Time getAvgTime() {
        return avgTime;
    }

    public QuotaModel setAvgTime(Time avgTime) {
        this.avgTime = avgTime;
        return this;
    }

    public int getUvCount() {
        return uvCount;
    }

    public QuotaModel setUvCount(int uvCount) {
        this.uvCount = uvCount;
        return this;
    }

    public int getUcCount() {
        return ucCount;
    }

    public QuotaModel setUcCount(int ucCount) {
        this.ucCount = ucCount;
        return this;
    }

    //    public long getAvgMillis() {
//        return avgMillis;
//    }
//
//    public QuotaModel setAvgMillis(long avgMillis) {
//        this.avgMillis = avgMillis;
//        return this;
//    }
}
