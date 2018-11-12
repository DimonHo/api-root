package com.wd.cloud.wdtjserver.utils;

import cn.hutool.core.lang.Console;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author He Zhigang
 * @date 2018/11/10
 * @Description:
 */
public class DateUtil extends cn.hutool.core.date.DateUtil {

    public static Map<Date,Double> getPro(Date beginTime,Date endTime){
        List<Date> hours = new ArrayList<>();
        while(beginTime.before(endTime)){
            hours.add(beginTime);
            beginTime = DateUtil.offsetHour(beginTime,1);
        }
        Console.log(hours);
        return null;
    }
}
