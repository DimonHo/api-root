package com.wd.cloud.orgserver.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author He Zhigang
 * @date 2018/8/10
 * @Description:
 */
@Entity
@Table(name = "ip_range")
public class IpRange extends AbstractEntity {
    @ManyToOne
    private EduOrg eduOrg;
    private String ipFrom;
    private String ipTo;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        IpRange ipRange = (IpRange) o;

        if (eduOrg != null ? !eduOrg.equals(ipRange.eduOrg) : ipRange.eduOrg != null) {
            return false;
        }
        if (ipFrom != null ? !ipFrom.equals(ipRange.ipFrom) : ipRange.ipFrom != null) {
            return false;
        }
        return ipTo != null ? ipTo.equals(ipRange.ipTo) : ipRange.ipTo == null;
    }

    @Override
    public int hashCode() {
        int result = eduOrg != null ? eduOrg.hashCode() : 0;
        result = 31 * result + (ipFrom != null ? ipFrom.hashCode() : 0);
        result = 31 * result + (ipTo != null ? ipTo.hashCode() : 0);
        return result;
    }
}
