package com.wd.cloud.wdtjserver.model;

import cn.hutool.core.date.DateTime;

import java.util.Date;

/**
 * @author He Zhigang
 * @date 2018/11/19
 * @Description: 指标总量
 */
public class TotalModel {

    private Long orgId;

    private String orgName;

    private DateTime date;

    private int pvTotal;

    private int scTotal;

    private int dcTotal;

    private int ddcTotal;

    private long visitTimeTotal;

    private int uvTotal;

    private int vvTotal;

    public Long getOrgId() {
        return orgId;
    }

    public TotalModel setOrgId(Long orgId) {
        this.orgId = orgId;
        return this;
    }

    public String getOrgName() {
        return orgName;
    }

    public TotalModel setOrgName(String orgName) {
        this.orgName = orgName;
        return this;
    }

    public DateTime getDate() {
        return date;
    }

    public TotalModel setDate(DateTime date) {
        this.date = date;
        return this;
    }

    public int getPvTotal() {
        return pvTotal;
    }

    public TotalModel setPvTotal(int pvTotal) {
        this.pvTotal = pvTotal;
        return this;
    }

    public int getScTotal() {
        return scTotal;
    }

    public TotalModel setScTotal(int scTotal) {
        this.scTotal = scTotal;
        return this;
    }

    public int getDcTotal() {
        return dcTotal;
    }

    public TotalModel setDcTotal(int dcTotal) {
        this.dcTotal = dcTotal;
        return this;
    }

    public int getDdcTotal() {
        return ddcTotal;
    }

    public TotalModel setDdcTotal(int ddcTotal) {
        this.ddcTotal = ddcTotal;
        return this;
    }

    public long getVisitTimeTotal() {
        return visitTimeTotal;
    }

    public TotalModel setVisitTimeTotal(long visitTimeTotal) {
        this.visitTimeTotal = visitTimeTotal;
        return this;
    }

    public int getUvTotal() {
        return uvTotal;
    }

    public TotalModel setUvTotal(int uvTotal) {
        this.uvTotal = uvTotal;
        return this;
    }

    public int getVvTotal() {
        return vvTotal;
    }

    public TotalModel setVvTotal(int vvTotal) {
        this.vvTotal = vvTotal;
        return this;
    }
}
