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
public class AbstractTjDataEntity implements Serializable {

    @EmbeddedId
    TjDataPk id;

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
