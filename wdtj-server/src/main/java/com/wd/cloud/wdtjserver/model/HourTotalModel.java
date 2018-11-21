package com.wd.cloud.wdtjserver.model;

import java.util.Date;

/**
 * @author He Zhigang
 * @date 2018/11/19
 * @Description: 小时数指标总量
 */
public class HourTotalModel {

    private Long orgId;
    
    private Date hourDate;
    
    private int pvTotal;

    private int scTotal;

    private int dcTotal;

    private int ddcTotal;

    private long visitTimeTotal;

    private int uvTotal;

    private int ucTotal;

    public Long getOrgId() {
        return orgId;
    }

    public HourTotalModel setOrgId(Long orgId) {
        this.orgId = orgId;
        return this;
    }

    public Date getHourDate() {
        return hourDate;
    }

    public HourTotalModel setHourDate(Date hourDate) {
        this.hourDate = hourDate;
        return this;
    }

    public int getPvTotal() {
        return pvTotal;
    }

    public HourTotalModel setPvTotal(int pvTotal) {
        this.pvTotal = pvTotal;
        return this;
    }

    public int getScTotal() {
        return scTotal;
    }

    public HourTotalModel setScTotal(int scTotal) {
        this.scTotal = scTotal;
        return this;
    }

    public int getDcTotal() {
        return dcTotal;
    }

    public HourTotalModel setDcTotal(int dcTotal) {
        this.dcTotal = dcTotal;
        return this;
    }

    public int getDdcTotal() {
        return ddcTotal;
    }

    public HourTotalModel setDdcTotal(int ddcTotal) {
        this.ddcTotal = ddcTotal;
        return this;
    }

    public long getVisitTimeTotal() {
        return visitTimeTotal;
    }

    public HourTotalModel setVisitTimeTotal(long visitTimeTotal) {
        this.visitTimeTotal = visitTimeTotal;
        return this;
    }

    public int getUvTotal() {
        return uvTotal;
    }

    public HourTotalModel setUvTotal(int uvTotal) {
        this.uvTotal = uvTotal;
        return this;
    }

    public int getUcTotal() {
        return ucTotal;
    }

    public HourTotalModel setUcTotal(int ucTotal) {
        this.ucTotal = ucTotal;
        return this;
    }
}
