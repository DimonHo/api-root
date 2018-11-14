package com.wd.cloud.commons.vo;

import java.io.Serializable;
import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/11/14
 * @Description:
 */
public class OrgVo implements Serializable {
    private Long id;
    private String name;
    private String flag;
    private List<IpRangeVo> ipRanges;

    public Long getId() {
        return id;
    }

    public OrgVo setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public OrgVo setName(String name) {
        this.name = name;
        return this;
    }

    public String getFlag() {
        return flag;
    }

    public OrgVo setFlag(String flag) {
        this.flag = flag;
        return this;
    }

    public List<IpRangeVo> getIpRanges() {
        return ipRanges;
    }

    public OrgVo setIpRanges(List<IpRangeVo> ipRanges) {
        this.ipRanges = ipRanges;
        return this;
    }
}
