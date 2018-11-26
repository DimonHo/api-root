package com.wd.cloud.wdtjserver;

import cn.hutool.core.lang.Console;
import com.wd.cloud.wdtjserver.entity.TjHisQuota;
import com.wd.cloud.wdtjserver.model.DateIntervalModel;
import com.wd.cloud.wdtjserver.service.TjService;
import com.wd.cloud.wdtjserver.service.impl.TjServiceImpl;
import com.wd.cloud.wdtjserver.utils.DateUtil;
import com.wd.cloud.wdtjserver.utils.RandomUtil;
import org.junit.Test;

import java.sql.Time;
import java.util.*;

/**
 * @author He Zhigang
 * @date 2018/11/10
 * @Description:
 */
public class JunitTest {

    @Test
    public void testWeight() {
        String beginTime = cn.hutool.core.date.DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:00");
        String endTime = cn.hutool.core.date.DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:59");
        Console.log("{},{}", beginTime, endTime);
//        long ti = 1448 - TimeZone.getDefault().getRawOffset();
//        Console.log(new Time(ti));
//        Console.log(Arrays.stream(new int[]{-3, 123, 1123, 1123, 33, 0}).max().orElse(0));
    }

    @Test
    public void testTime() {

        List<Long> arr = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Console.log(RandomUtil.randomInt(1));
        }
        while (arr.size() > 0) {
            Console.log(RandomUtil.randomLongEles(arr, 3, true).orElse(new ArrayList<>()));
        }
    }


    @Test
    public void test3() {
        double a = 23.1123523412;
        double b = 23.1123523412;

        Console.log(a == b);
        Console.log(DateUtil.parse("2018-11-01").equals(DateUtil.parse("2018-11-01").toJdkDate()));
    }

    @Test
    public void test1() {
//        Map<DateTime, Double> map = new TreeMap<>();
//        DateTime now = DateUtil.date();
//        for (int i = 0; i < 24; i++) {
//            map.put(DateUtil.offsetMinute(now, 1), RandomUtil.randomDouble(2));
//            now = DateUtil.offsetMinute(now, 1);
//        }
        TjService tjService = new TjServiceImpl();
        TjHisQuota tjHisQuota = new TjHisQuota();
        tjHisQuota.setPvCount(50000);
        tjHisQuota.setBeginTime(DateUtil.parse("2018-11-01"));
        tjHisQuota.setBeginTime(DateUtil.parse("2018-11-30"));
        //tjService.buildTjHisData(tjHisQuota);
//        Map<DateTime, Integer> result = RandomUtil.randomMapFromWeight(100000, map, 0.4);
//        int count = 0;
//        for (Map.Entry<DateTime, Integer> entry : result.entrySet()) {
//            count += entry.getValue();
//        }
//        Console.log(count);
    }


    @Test
    public void testRandom() {
        DateIntervalModel model1 = new DateIntervalModel(DateUtil.parse("2018-11-01"), DateUtil.parse("2018-11-30"));
        DateIntervalModel model2 = new DateIntervalModel(DateUtil.parse("2018-11-11"), DateUtil.parse("2018-12-30"));

        Console.log(DateUtil.overlapDate(model1, model2));

        int a = 100;
        float b = 24 * 60;
        float c = a / b;
        Console.log(c);
        //Console.log(DateUtil.getTomorrowMap());
        //List<DateTime> dateList = DateUtil.rangeToList(DateUtil.beginOfDay(DateUtil.tomorrow()),DateUtil.endOfDay(DateUtil.tomorrow()), DateField.MINUTE);
        //Console.log(dateList);
        //Console.log(DateUtil.getMonthDaysJson(DateUtil.parse("2018-11-01"),DateUtil.parse("2018-11-30")));
    }

//    public Map<String,Map<String, Map<String,Double>>> getMonthDaysMap(Date beginDate, Date endDate){
//        Map<String,Map<String,Map<String,Double>>> months = new TreeMap();
//        while(endDate.after(beginDate)){
//            Map<String,Map<String,Double>> days = new TreeMap();
//            String month = DateUtil.format(beginDate,"yyyy-MM");
//            Date lastDay = endDate.after(DateUtil.endOfMonth(beginDate))?DateUtil.endOfMonth(beginDate):endDate;
//            while(lastDay.after(beginDate)){
//                Map<String,Double> hours = new TreeMap();
//                String day = DateUtil.format(beginDate,"yyyy-MM-dd");
//                Date lasthours = endDate.after(DateUtil.endOfDay(beginDate))?DateUtil.endOfDay(beginDate):DateUtil.offsetMillisecond(endDate,1);
//                while(lasthours.after(beginDate)){
//                    if (global.getHighMonths() != null && global.getHighMonths().getOptions().contains(DateUtil.month(beginDate))){
//                        if (global.getHighDays() != null && global.getHighDays().getOptions().contains(DateUtil.dayOfMonth(beginDate))){
//                            if (global.getHighHours() != null && global.getHighHours().getOptions().contains(DateUtil.hour(beginDate,true))){
//                                hours.put(DateUtil.formatTime(beginDate),global.getHighMonths().getProportion() * global.getHighDays().getProportion() * global.getHighHours().getProportion());
//                            } else if (global.getLowHours() != null && global.getLowHours().getOptions().contains(DateUtil.hour(beginDate,true))){
//                                hours.put(DateUtil.formatTime(beginDate),global.getHighMonths().getProportion() * global.getHighDays().getProportion() * global.getLowHours().getProportion());
//                            } else {
//                                hours.put(DateUtil.formatTime(beginDate),global.getHighMonths().getProportion() * global.getHighDays().getProportion());
//                            }
//                        } else if (global.getLowDays() != null && global.getLowDays().getOptions().contains(DateUtil.dayOfMonth(beginDate))){
//                            if (global.getHighHours() != null && global.getHighHours().getOptions().contains(DateUtil.hour(beginDate,true))){
//                                hours.put(DateUtil.formatTime(beginDate),global.getHighMonths().getProportion() * global.getLowDays().getProportion() * global.getHighHours().getProportion());
//                            } else if (global.getLowHours() != null && global.getLowHours().getOptions().contains(DateUtil.hour(beginDate,true))){
//                                hours.put(DateUtil.formatTime(beginDate),global.getHighMonths().getProportion() * global.getLowDays().getProportion() * global.getLowHours().getProportion());
//                            } else {
//                                hours.put(DateUtil.formatTime(beginDate),global.getHighMonths().getProportion() * global.getLowDays().getProportion());
//                            }
//                        } else{
//                            if (global.getHighHours() != null && global.getHighHours().getOptions().contains(DateUtil.hour(beginDate,true))){
//                                hours.put(DateUtil.formatTime(beginDate),global.getHighMonths().getProportion()  * global.getHighHours().getProportion());
//                            } else if (global.getLowHours() != null && global.getLowHours().getOptions().contains(DateUtil.hour(beginDate,true))){
//                                hours.put(DateUtil.formatTime(beginDate),global.getHighMonths().getProportion() * global.getLowHours().getProportion());
//                            } else {
//                                hours.put(DateUtil.formatTime(beginDate),global.getHighMonths().getProportion());
//                            }
//                        }
//                    }else if (global.getLowMonths() != null && global.getLowMonths().getOptions().contains(DateUtil.month(beginDate))){
//                        if (global.getHighDays() != null && global.getHighDays().getOptions().contains(DateUtil.dayOfMonth(beginDate))){
//                            if (global.getHighHours() != null && global.getHighHours().getOptions().contains(DateUtil.hour(beginDate,true))){
//                                hours.put(DateUtil.formatTime(beginDate),global.getLowMonths().getProportion() * global.getHighDays().getProportion() * global.getHighHours().getProportion());
//                            } else if (global.getLowHours() != null && global.getLowHours().getOptions().contains(DateUtil.hour(beginDate,true))){
//                                hours.put(DateUtil.formatTime(beginDate),global.getLowMonths().getProportion() * global.getHighDays().getProportion() * global.getLowHours().getProportion());
//                            } else {
//                                hours.put(DateUtil.formatTime(beginDate),global.getLowMonths().getProportion() * global.getHighDays().getProportion());
//                            }
//                        } else if (global.getLowDays() != null && global.getLowDays().getOptions().contains(DateUtil.dayOfMonth(beginDate))){
//                            if (global.getHighHours() != null && global.getHighHours().getOptions().contains(DateUtil.hour(beginDate,true))){
//                                hours.put(DateUtil.formatTime(beginDate),global.getLowMonths().getProportion() * global.getLowDays().getProportion() * global.getHighHours().getProportion());
//                            } else if (global.getLowHours() != null && global.getLowHours().getOptions().contains(DateUtil.hour(beginDate,true))){
//                                hours.put(DateUtil.formatTime(beginDate),global.getLowMonths().getProportion() * global.getLowDays().getProportion() * global.getLowHours().getProportion());
//                            } else {
//                                hours.put(DateUtil.formatTime(beginDate),global.getLowMonths().getProportion() * global.getLowDays().getProportion());
//                            }
//                        } else{
//                            if (global.getHighHours() != null && global.getHighHours().getOptions().contains(DateUtil.hour(beginDate,true))){
//                                hours.put(DateUtil.formatTime(beginDate),global.getLowMonths().getProportion()  * global.getHighHours().getProportion());
//                            } else if (global.getLowHours() != null && global.getLowHours().getOptions().contains(DateUtil.hour(beginDate,true))){
//                                hours.put(DateUtil.formatTime(beginDate),global.getLowMonths().getProportion() * global.getLowHours().getProportion());
//                            } else {
//                                hours.put(DateUtil.formatTime(beginDate),global.getLowMonths().getProportion());
//                            }
//                        }
//                    }else{
//                        if (global.getHighDays() != null && global.getHighDays().getOptions().contains(DateUtil.dayOfMonth(beginDate))){
//                            if (global.getHighHours() != null && global.getHighHours().getOptions().contains(DateUtil.hour(beginDate,true))){
//                                hours.put(DateUtil.formatTime(beginDate), global.getHighDays().getProportion() * global.getHighHours().getProportion());
//                            } else if (global.getLowHours() != null && global.getLowHours().getOptions().contains(DateUtil.hour(beginDate,true))){
//                                hours.put(DateUtil.formatTime(beginDate),global.getHighDays().getProportion() * global.getLowHours().getProportion());
//                            } else {
//                                hours.put(DateUtil.formatTime(beginDate),global.getHighDays().getProportion());
//                            }
//                        } else if (global.getLowDays() != null && global.getLowDays().getOptions().contains(DateUtil.dayOfMonth(beginDate))){
//                            if (global.getHighHours() != null && global.getHighHours().getOptions().contains(DateUtil.hour(beginDate,true))){
//                                hours.put(DateUtil.formatTime(beginDate),global.getLowDays().getProportion() * global.getHighHours().getProportion());
//                            } else if (global.getLowHours() != null && global.getLowHours().getOptions().contains(DateUtil.hour(beginDate,true))){
//                                hours.put(DateUtil.formatTime(beginDate),global.getLowDays().getProportion() * global.getLowHours().getProportion());
//                            } else {
//                                hours.put(DateUtil.formatTime(beginDate),global.getLowDays().getProportion());
//                            }
//                        } else{
//                            if (global.getHighHours() != null && global.getHighHours().getOptions().contains(DateUtil.hour(beginDate,true))){
//                                hours.put(DateUtil.formatTime(beginDate),global.getHighHours().getProportion());
//                            } else if (global.getLowHours() != null && global.getLowHours().getOptions().contains(DateUtil.hour(beginDate,true))){
//                                hours.put(DateUtil.formatTime(beginDate),global.getLowHours().getProportion());
//                            } else {
//                                hours.put(DateUtil.formatTime(beginDate),1.0);
//                            }
//                        }
//                    }
//                    beginDate = DateUtil.offsetHour(beginDate,1);
//                }
//                days.put(day,hours);
//            }
//            months.put(month,days);
//        }
//        Console.log(months);
//        return months;
//    }
}
