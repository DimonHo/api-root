package com.wd.cloud.wdtjserver.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Date;

/**
 * @author He Zhigang
 * @date 2018/11/18
 * @Description: tjData表的联合主键
 */
@Embeddable
public class TjDataPk implements Serializable {

    @Column(name = "org_id")
    private Long orgId;

    @Column(name = "tj_date")
    private Date tjDate;

    public TjDataPk(){
    }

    public TjDataPk(Long orgId, Date tjDate) {
        this.orgId = orgId;
        this.tjDate = tjDate;
    }

    public Long getOrgId() {
        return orgId;
    }

    public TjDataPk setOrgId(Long orgId) {
        this.orgId = orgId;
        return this;
    }

    public Date getTjDate() {
        return tjDate;
    }

    public TjDataPk setTjDate(Date tjDate) {
        this.tjDate = tjDate;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TjDataPk tjDataPk = (TjDataPk) o;

        if (orgId != null ? !orgId.equals(tjDataPk.orgId) : tjDataPk.orgId != null) {
            return false;
        }
        return tjDate != null ? tjDate.equals(tjDataPk.tjDate) : tjDataPk.tjDate == null;
    }

    @Override
    public int hashCode() {
        int result = orgId != null ? orgId.hashCode() : 0;
        result = 31 * result + (tjDate != null ? tjDate.hashCode() : 0);
        return result;
    }
}
