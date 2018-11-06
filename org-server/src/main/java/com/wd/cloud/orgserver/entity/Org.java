package com.wd.cloud.orgserver.entity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

/**
 * @author He Zhigang
 * @date 2018/8/10
 * @Description:
 */
@Entity
@Table(name = "org")
public class Org extends AbstractEntity {

    private String orgFlag;
    private String orgName;

    @OneToMany(mappedBy = "org")
    private Set<IpRange> ipRanges;

}
