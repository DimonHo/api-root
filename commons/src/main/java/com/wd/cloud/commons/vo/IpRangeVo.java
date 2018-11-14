package com.wd.cloud.commons.vo;

import java.io.Serializable;

/**
 * @author He Zhigang
 * @date 2018/11/14
 * @Description:
 */
public class IpRangeVo implements Serializable {

    private String begin;
    private String end;

    public String getBegin() {
        return begin;
    }

    public IpRangeVo setBegin(String begin) {
        this.begin = begin;
        return this;
    }

    public String getEnd() {
        return end;
    }

    public IpRangeVo setEnd(String end) {
        this.end = end;
        return this;
    }
}
