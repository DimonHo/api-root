package com.wd.cloud.wdtjserver.utils;

import com.wd.cloud.wdtjserver.model.DateIntervalModel;

import java.util.*;

/**
 * @author He Zhigang
 * @date 2018/11/10
 * @Description:
 */
public class DateUtil extends cn.hutool.core.date.DateUtil {

    /**
     * 重叠时段
     *
     * @param intervalModel1
     * @param intervalModel2
     * @return
     */
    public static DateIntervalModel overlapDate(DateIntervalModel intervalModel1, DateIntervalModel intervalModel2) {
        Date start1 = intervalModel1.getBeginDate();
        Date end1 = intervalModel1.getEndDate();
        Date start2 = intervalModel2.getBeginDate();
        Date end2 = intervalModel2.getEndDate();
        // 开始时间比较
        if (start2.after(start1)) {
            start1 = start2;
        }
        if (end2.before(end1)) {
            end1 = end2;
        }
        if (start1.before(end1)) {
            return new DateIntervalModel(start1, end1);
        }
        return null;
    }

}
