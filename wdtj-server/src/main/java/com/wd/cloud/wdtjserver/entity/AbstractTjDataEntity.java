package com.wd.cloud.wdtjserver.entity;

import lombok.Data;
import lombok.experimental.Accessors;
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
@Data
@Accessors(chain = true)
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
}
