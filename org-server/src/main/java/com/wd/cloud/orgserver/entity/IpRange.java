package com.wd.cloud.orgserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author He Zhigang
 * @date 2018/8/10
 * @Description: IP范围
 */
@Entity
@Table(name = "ip_range")
public class IpRange extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "org_id")
    @JsonIgnore
    private Org org;

    private String begin;

    private String end;

    public Org getOrg() {
        return org;
    }

    public IpRange setOrg(Org org) {
        this.org = org;
        return this;
    }

    public String getBegin() {
        return begin;
    }

    public IpRange setBegin(String begin) {
        this.begin = begin;
        return this;
    }

    public String getEnd() {
        return end;
    }

    public IpRange setEnd(String end) {
        this.end = end;
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

        IpRange ipRange = (IpRange) o;

        if (org != null ? !org.equals(ipRange.org) : ipRange.org != null) {
            return false;
        }
        if (begin != null ? !begin.equals(ipRange.begin) : ipRange.begin != null) {
            return false;
        }
        return end != null ? end.equals(ipRange.end) : ipRange.end == null;
    }

    @Override
    public int hashCode() {
        int result = org != null ? org.hashCode() : 0;
        result = 31 * result + (begin != null ? begin.hashCode() : 0);
        result = 31 * result + (end != null ? end.hashCode() : 0);
        return result;
    }
}
