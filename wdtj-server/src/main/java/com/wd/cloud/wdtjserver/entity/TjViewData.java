package com.wd.cloud.wdtjserver.entity;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description: 展示给用户的最终数据
 */
@Entity
@Table(name = "tj_view_data", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"org_id", "tj_date"})
})
public class TjViewData extends AbstractEntity {

    /**
     * 机构ID
     */
    @Column(name = "org_id")
    private Long orgId;

    private int pvCount;
    private int scCount;
    private int dcCount;
    private int ddcCount;
    private Time avgTime;

    /**
     * 时间
     */
    @Column(name = "tj_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date tjDate;

    public Long getOrgId() {
        return orgId;
    }

    public TjViewData setOrgId(Long orgId) {
        this.orgId = orgId;
        return this;
    }

    public int getPvCount() {
        return pvCount;
    }

    public TjViewData setPvCount(int pvCount) {
        this.pvCount = pvCount;
        return this;
    }

    public int getScCount() {
        return scCount;
    }

    public TjViewData setScCount(int scCount) {
        this.scCount = scCount;
        return this;
    }

    public int getDcCount() {
        return dcCount;
    }

    public TjViewData setDcCount(int dcCount) {
        this.dcCount = dcCount;
        return this;
    }

    public int getDdcCount() {
        return ddcCount;
    }

    public TjViewData setDdcCount(int ddcCount) {
        this.ddcCount = ddcCount;
        return this;
    }

    public Time getAvgTime() {
        return avgTime;
    }

    public TjViewData setAvgTime(Time avgTime) {
        this.avgTime = avgTime;
        return this;
    }

    public Date getTjDate() {
        return tjDate;
    }

    public TjViewData setTjDate(Date tjDate) {
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

        TjViewData that = (TjViewData) o;

        if (pvCount != that.pvCount) {
            return false;
        }
        if (scCount != that.scCount) {
            return false;
        }
        if (dcCount != that.dcCount) {
            return false;
        }
        if (ddcCount != that.ddcCount) {
            return false;
        }
        if (orgId != null ? !orgId.equals(that.orgId) : that.orgId != null) {
            return false;
        }
        if (avgTime != null ? !avgTime.equals(that.avgTime) : that.avgTime != null) {
            return false;
        }
        return tjDate != null ? tjDate.equals(that.tjDate) : that.tjDate == null;
    }

    @Override
    public int hashCode() {
        int result = orgId != null ? orgId.hashCode() : 0;
        result = 31 * result + pvCount;
        result = 31 * result + scCount;
        result = 31 * result + dcCount;
        result = 31 * result + ddcCount;
        result = 31 * result + (avgTime != null ? avgTime.hashCode() : 0);
        result = 31 * result + (tjDate != null ? tjDate.hashCode() : 0);
        return result;
    }
}
