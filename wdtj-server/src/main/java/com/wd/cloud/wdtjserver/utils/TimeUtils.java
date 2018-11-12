package com.wd.cloud.wdtjserver.utils;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by hjz on 2017/8/7 0007.
 * Describe：
 * 统计两个日期时间段每个月对应的天数
 * 比如：2017-03-12 ～ 2018-12-18
 * 2017-03-12到该月月底共有多少天
 * 2017-04该月有多少天
 * 。。。
 */
public class TimeUtils {

    public static Map<String, Object> get(Date maxDate,Date minDate){

        Calendar max = Calendar.getInstance();
        max.setTime(maxDate);
        max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);
        Calendar min = Calendar.getInstance();
        min.setTime(minDate);
        min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);
        Calendar curr = min;
        List<Date> result = new ArrayList<>();
        while (curr.before(max)) {
            result.add(curr.getTime());
            curr.add(Calendar.MONTH, 1);
        }
        String day = "";
        Date minMothDate = null;
        Date maxMothDate = null;
        Calendar currMonthCal = Calendar.getInstance();
        Map<String,Object> map=new HashMap<>();
        for (int i=0; i<result.size(); i++){
            if (i == 0){  //处理开始日期时间
                if (result.size() == 1){   //处理开始时间和结束时间是同年同月的情况
                    minMothDate = minDate;
                    maxMothDate = maxDate;
                }else {
                    minMothDate = minDate;
                    currMonthCal.setTime(result.get(i));
                    //设置当前月最后一天
                    currMonthCal.set(Calendar.DAY_OF_MONTH, currMonthCal.getActualMaximum(Calendar.DAY_OF_MONTH));
                    maxMothDate = currMonthCal.getTime();
                }
                day = getTwoDay(maxMothDate,minMothDate);
            }else if (i == result.size()-1){ //处理最后一次时间
                maxMothDate = maxDate;
                currMonthCal.setTime(result.get(i));
                currMonthCal.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天
                minMothDate = currMonthCal.getTime();
                day = getTwoDay(maxMothDate,minMothDate);
            }else{
                currMonthCal.setTime(result.get(i));
                currMonthCal.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天
                minMothDate = currMonthCal.getTime();
                //设置当前月最后一天
                currMonthCal.set(Calendar.DAY_OF_MONTH, currMonthCal.getActualMaximum(Calendar.DAY_OF_MONTH));
                maxMothDate = currMonthCal.getTime();
                day = getTwoDay(maxMothDate,minMothDate);
            }
            System.out.println(currMonthCal.get(Calendar.YEAR )+"年"+(currMonthCal.get(Calendar.MONTH )+1)+"月共 "+day +" 天");
            map.put("day"+i,day);
            map.put("month"+i,(currMonthCal.get(Calendar.MONTH )+1));
            map.put("year"+i,currMonthCal.get(Calendar.YEAR ));
        }
        map.put("result",result.size());
        return map;
    }
    /**
     * 得到二个日期间的间隔天数
     * @param endTime  结束时间
     * @param startTime 开始时间
     * @return
     */
    public static String getTwoDay(Date endTime, Date startTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        System.out.println( "startTime="+format.format(startTime)+ " endTime="+format.format(endTime) );
        long day = 0;
        try {
            day = (endTime.getTime() - startTime.getTime()) / (24 * 60 * 60 * 1000)+1;
        } catch (Exception e) {
            return "";
        }
        return day + "";
    }
}
