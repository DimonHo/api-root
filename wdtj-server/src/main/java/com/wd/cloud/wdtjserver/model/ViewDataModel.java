package com.wd.cloud.wdtjserver.model;

import io.swagger.annotations.ApiModel;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description: 前台展示数据模型
 */
@ApiModel(value = "返回给前台数据对象")
public class ViewDataModel {

    private Long orgId;

    private List<String> tjDate = new ArrayList<>();

    private List<BigDecimal> pvCount = new ArrayList<>();

    private List<BigDecimal> scCount = new ArrayList<>();

    private List<BigDecimal> dcCount = new ArrayList<>();

    private List<BigDecimal> ddcCount = new ArrayList<>();

    private List<BigDecimal> uvCount = new ArrayList<>();

    private List<BigDecimal> ucCount = new ArrayList<>();

    private List<Time> avgTime = new ArrayList<>();

    public Long getOrgId() {
        return orgId;
    }

    public ViewDataModel setOrgId(Long orgId) {
        this.orgId = orgId;
        return this;
    }

    public List<String> getTjDate() {
        return tjDate;
    }

    public ViewDataModel setTjDate(List<String> tjDate) {
        this.tjDate = tjDate;
        return this;
    }

    public List<BigDecimal> getPvCount() {
        return pvCount;
    }

    public ViewDataModel setPvCount(List<BigDecimal> pvCount) {
        this.pvCount = pvCount;
        return this;
    }

    public List<BigDecimal> getScCount() {
        return scCount;
    }

    public ViewDataModel setScCount(List<BigDecimal> scCount) {
        this.scCount = scCount;
        return this;
    }

    public List<BigDecimal> getDcCount() {
        return dcCount;
    }

    public ViewDataModel setDcCount(List<BigDecimal> dcCount) {
        this.dcCount = dcCount;
        return this;
    }

    public List<BigDecimal> getDdcCount() {
        return ddcCount;
    }

    public ViewDataModel setDdcCount(List<BigDecimal> ddcCount) {
        this.ddcCount = ddcCount;
        return this;
    }

    public List<BigDecimal> getUvCount() {
        return uvCount;
    }

    public ViewDataModel setUvCount(List<BigDecimal> uvCount) {
        this.uvCount = uvCount;
        return this;
    }

    public List<BigDecimal> getUcCount() {
        return ucCount;
    }

    public ViewDataModel setUcCount(List<BigDecimal> ucCount) {
        this.ucCount = ucCount;
        return this;
    }

    public List<Time> getAvgTime() {
        return avgTime;
    }

    public ViewDataModel setAvgTime(List<Time> avgTime) {
        this.avgTime = avgTime;
        return this;
    }
}
