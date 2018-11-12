package com.wd.cloud.wdtjserver.utils;

import cn.hutool.core.lang.Console;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.sun.org.apache.bcel.internal.generic.RETURN;

import java.util.*;

/**
 * @author He Zhigang
 * @date 2018/11/10
 * @Description:
 */
public class DateUtil extends cn.hutool.core.date.DateUtil {


    public static Map<String,Map<String, List<String>>> getTomorrowMap(){
        return getMonthDaysMap(DateUtil.beginOfDay(DateUtil.tomorrow()),DateUtil.endOfDay(DateUtil.tomorrow()));
    }

    public static  Map<String,Map<String, List<String>>> getMonthDaysMap(Date beginDate, Date endDate){
        Map<String,Map<String,List<String>>> months = new TreeMap();
        while(endDate.after(beginDate)){
            Map<String,List<String>> days = new TreeMap();
            String month = DateUtil.format(beginDate,"yyyy-MM");
            Date lastDay = endDate.after(DateUtil.endOfMonth(beginDate))?DateUtil.endOfMonth(beginDate):endDate;
            while(lastDay.after(beginDate)){
                List<String> hours = new ArrayList<>();
                String day = DateUtil.format(beginDate,"yyyy-MM-dd");
                Date lasthours = endDate.after(DateUtil.endOfDay(beginDate))?DateUtil.endOfDay(beginDate):DateUtil.offsetMillisecond(endDate,1);
                while(lasthours.after(beginDate)){
                    hours.add(DateUtil.formatTime(beginDate));
                    beginDate = DateUtil.offsetHour(beginDate,1);
                }
                days.put(day,hours);
            }
            months.put(month,days);
        }
        return months;
    }




}
