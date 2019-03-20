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

    @Column(name = "org_falg")
    private String orgFlag;

    @Column(name = "tj_date")
    private Date tjDate;

    public TjDataPk() {
    }

    public TjDataPk(String orgFlag, Date tjDate) {
        this.orgFlag = orgFlag;
        this.tjDate = tjDate;
    }

    public String getOrgFlag() {
        return orgFlag;
    }

    public TjDataPk setOrgFlag(String orgFlag) {
        this.orgFlag = orgFlag;
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

        if (orgFlag != null ? !orgFlag.equals(tjDataPk.orgFlag) : tjDataPk.orgFlag != null) {
            return false;
        }
        return tjDate != null ? tjDate.equals(tjDataPk.tjDate) : tjDataPk.tjDate == null;
    }

    @Override
    public int hashCode() {
        int result = orgFlag != null ? orgFlag.hashCode() : 0;
        result = 31 * result + (tjDate != null ? tjDate.hashCode() : 0);
        return result;
    }
}
