package com.wd.cloud.wdtjserver.model;

import io.swagger.annotations.ApiModel;

import java.util.ArrayList;
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

    private Integer pvTotal;

    private List<Integer> pvCount = new ArrayList<>();

    private Integer scTotal;

    private List<Integer> scCount = new ArrayList<>();

    private Integer dcTotal;

    private List<Integer> dcCount = new ArrayList<>();

    private Integer ddcTotal;

    private List<Integer> ddcCount = new ArrayList<>();

    private Integer uvTotal;

    private List<Integer> uvCount = new ArrayList<>();

    private Integer ucTotal;

    private List<Integer> ucCount = new ArrayList<>();

    private Long avgTimeTotal;

    private List<Long> avgTime = new ArrayList<>();

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

    public Integer getPvTotal() {
        return pvTotal;
    }

    public ViewDataModel setPvTotal(Integer pvTotal) {
        this.pvTotal = pvTotal;
        return this;
    }

    public Integer getScTotal() {
        return scTotal;
    }

    public ViewDataModel setScTotal(Integer scTotal) {
        this.scTotal = scTotal;
        return this;
    }

    public Integer getDcTotal() {
        return dcTotal;
    }

    public ViewDataModel setDcTotal(Integer dcTotal) {
        this.dcTotal = dcTotal;
        return this;
    }

    public Integer getDdcTotal() {
        return ddcTotal;
    }

    public ViewDataModel setDdcTotal(Integer ddcTotal) {
        this.ddcTotal = ddcTotal;
        return this;
    }

    public Integer getUvTotal() {
        return uvTotal;
    }

    public ViewDataModel setUvTotal(Integer uvTotal) {
        this.uvTotal = uvTotal;
        return this;
    }

    public Integer getUcTotal() {
        return ucTotal;
    }

    public ViewDataModel setUcTotal(Integer ucTotal) {
        this.ucTotal = ucTotal;
        return this;
    }

    public Long getAvgTimeTotal() {
        return avgTimeTotal;
    }

    public ViewDataModel setAvgTimeTotal(Long avgTimeTotal) {
        this.avgTimeTotal = avgTimeTotal;
        return this;
    }

    public List<Long> getAvgTime() {
        return avgTime;
    }

    public ViewDataModel setAvgTime(List<Long> avgTime) {
        this.avgTime = avgTime;
        return this;
    }

    /**
     * 计算总量
     * @return
     */
    public void sumTotal() {
        this.setPvTotal(this.getPvCount().stream().reduce((a, b) -> a + b).orElse(0));
        this.setScTotal(this.getScCount().stream().reduce((a, b) -> a + b).orElse(0));
        this.setDcTotal(this.getDcCount().stream().reduce((a, b) -> a + b).orElse(0));
        this.setDdcTotal(this.getDdcCount().stream().reduce((a, b) -> a + b).orElse(0));
        this.setUvTotal(this.getUvCount().stream().reduce((a, b) -> a + b).orElse(0));
        this.setUcTotal(this.getUcCount().stream().reduce((a, b) -> a + b).orElse(0));
        long avgTotal = this.getUcTotal() == 0 ? 0 : this.getAvgTime().stream().reduce((a, b) -> a + b).orElse(0L) / this.getUcTotal();
        this.setAvgTimeTotal(avgTotal);
    }
}
