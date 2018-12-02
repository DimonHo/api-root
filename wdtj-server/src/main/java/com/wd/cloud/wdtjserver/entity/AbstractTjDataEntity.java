package com.wd.cloud.wdtjserver.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.EmbeddedId;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

/**
 * @author He Zhigang
 * @date 2018/11/18
 * @Description:
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractTjDataEntity implements Serializable {

    @EmbeddedId
    TjDataPk id;

    String orgName;

    int pvCount;

    int scCount;

    int dcCount;

    int ddcCount;

    int uvCount;

    int vvCount;

    long visitTime;

    @LastModifiedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date gmtModified;

    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date gmtCreate;

    public TjDataPk getId() {
        return id;
    }

    public AbstractTjDataEntity setId(TjDataPk id) {
        this.id = id;
        return this;
    }

    public String getOrgName() {
        return orgName;
    }

    public AbstractTjDataEntity setOrgName(String orgName) {
        this.orgName = orgName;
        return this;
    }

    public int getPvCount() {
        return pvCount;
    }

    public AbstractTjDataEntity setPvCount(int pvCount) {
        this.pvCount = pvCount;
        return this;
    }

    public int getScCount() {
        return scCount;
    }

    public AbstractTjDataEntity setScCount(int scCount) {
        this.scCount = scCount;
        return this;
    }

    public int getDcCount() {
        return dcCount;
    }

    public AbstractTjDataEntity setDcCount(int dcCount) {
        this.dcCount = dcCount;
        return this;
    }

    public int getDdcCount() {
        return ddcCount;
    }

    public AbstractTjDataEntity setDdcCount(int ddcCount) {
        this.ddcCount = ddcCount;
        return this;
    }

    public int getUvCount() {
        return uvCount;
    }

    public AbstractTjDataEntity setUvCount(int uvCount) {
        this.uvCount = uvCount;
        return this;
    }

    public int getVvCount() {
        return vvCount;
    }

    public AbstractTjDataEntity setVvCount(int vvCount) {
        this.vvCount = vvCount;
        return this;
    }

    public long getVisitTime() {
        return visitTime;
    }

    public AbstractTjDataEntity setVisitTime(long visitTime) {
        this.visitTime = visitTime;
        return this;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public AbstractTjDataEntity setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
        return this;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public AbstractTjDataEntity setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
        return this;
    }
}
