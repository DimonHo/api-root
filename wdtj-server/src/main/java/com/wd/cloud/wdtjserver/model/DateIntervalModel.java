package com.wd.cloud.wdtjserver.model;

import cn.hutool.core.date.DateUtil;

import java.util.Date;
import java.util.StringJoiner;

/**
 * @author He Zhigang
 * @date 2018/11/12
 * @Description: 时间区间模型
 */
public class DateIntervalModel {

    private Date beginDate;

    private Date endDate;

    public DateIntervalModel(Date beginDate, Date endDate) {
        if (beginDate.after(endDate)) {
            this.beginDate = endDate;
            this.endDate = beginDate;
        }else{
            this.beginDate = beginDate;
            this.endDate = endDate;
        }
    }

    public DateIntervalModel(String beginDate, String endDate) {
        this(DateUtil.parse(beginDate), DateUtil.parse(endDate));
    }

    public DateIntervalModel(long beginDate, long endDate) {
        this(new Date(beginDate), new Date(endDate));
    }


    public Date getBeginDate() {
        return beginDate;
    }

    public DateIntervalModel setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
        return this;
    }

    public Date getEndDate() {
        return endDate;
    }

    public DateIntervalModel setEndDate(Date endDate) {
        this.endDate = endDate;
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DateIntervalModel.class.getSimpleName() + "[", "]")
                .add("beginDate=" + DateUtil.formatDateTime(beginDate))
                .add("endDate=" + DateUtil.formatDateTime(endDate))
                .toString();
    }
}
