package com.wd.cloud.wdtjserver.model;

import io.swagger.annotations.ApiModel;

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

    private List<Integer> pvCount = new ArrayList<>();

    private List<Integer> scCount = new ArrayList<>();

    private List<Integer> dcCount = new ArrayList<>();

    private List<Integer> ddcCount = new ArrayList<>();

    private List<Integer> uvCount = new ArrayList<>();

    private List<Integer> ucCount = new ArrayList<>();

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

    public List<Integer> getPvCount() {
        return pvCount;
    }

    public ViewDataModel setPvCount(List<Integer> pvCount) {
        this.pvCount = pvCount;
        return this;
    }

    public List<Integer> getScCount() {
        return scCount;
    }

    public ViewDataModel setScCount(List<Integer> scCount) {
        this.scCount = scCount;
        return this;
    }

    public List<Integer> getDcCount() {
        return dcCount;
    }

    public ViewDataModel setDcCount(List<Integer> dcCount) {
        this.dcCount = dcCount;
        return this;
    }

    public List<Integer> getDdcCount() {
        return ddcCount;
    }

    public ViewDataModel setDdcCount(List<Integer> ddcCount) {
        this.ddcCount = ddcCount;
        return this;
    }

    public List<Integer> getUvCount() {
        return uvCount;
    }

    public ViewDataModel setUvCount(List<Integer> uvCount) {
        this.uvCount = uvCount;
        return this;
    }

    public List<Integer> getUcCount() {
        return ucCount;
    }

    public ViewDataModel setUcCount(List<Integer> ucCount) {
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
