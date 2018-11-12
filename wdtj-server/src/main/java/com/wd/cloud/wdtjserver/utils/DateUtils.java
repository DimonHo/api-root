package com.wd.cloud.wdtjserver.utils;


import com.wd.cloud.wdtjserver.config.GlobalConfig;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DateUtils {
    @Autowired
    GlobalConfig globalConfig;

    /**
     * 根据开始时间，结束时间获取小时差
     *
     * @param endTime
     * @param beginTime
     * @return
     */
    public static int getHoursNum(String endTime, String beginTime) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date now = null;
        Date date = null;
        try {
            now = df.parse(endTime);
            date = df.parse(beginTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long l = now.getTime() - date.getTime();
        int hours = (int) (l / (60 * 60 * 1000));
        return hours;
    }

    /**
     * 获取当前时间后一天时间
     * @param date
     * @return
     */
    public static String getNextDay(String date) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,1);
        date = sf.format(calendar.getTime());
        return date;
    }

    /**
     * 高峰期月份
     *
     * @return
     */
    public  int[] optionsHighMonth() {
        int[] optionsHighMonth = globalConfig.getHighMonths().getOptions();
        return optionsHighMonth;
    }

    /**
     * 高峰期日期
     *
     * @return
     */
    public int[] optionHighDay() {
        int[] optionHighDay = globalConfig.getHighDays().getOptions();
        return optionHighDay;
    }

    /**
     * 高峰期时钟
     *
     * @return
     */
    public int[] optionHighHours() {
        int[] optionHighHours = globalConfig.getHighHours().getOptions();
        return optionHighHours;
    }

    /**
     * 低峰期日期
     *
     * @return
     */
    public int[] optionLowDay() {
        int[] optionLowDay = globalConfig.getLowDays().getOptions();
        return optionLowDay;
    }

    /**
     * 低峰期时钟
     *
     * @return
     */
    public int[] optionLowHours() {
        int[] optionLowHours = globalConfig.getLowHours().getOptions();
        return optionLowHours;
    }

    /**
     * 低峰期月份
     *
     * @return
     */
    public int[] optionLowMonths() {
        int[] optionsLowMonths = globalConfig.getLowMonths().getOptions();
        return optionsLowMonths;
    }

    /**
     * 高峰期月份倍数
     *
     * @return
     */
    public double proportionHighMonth() {
        double proportionHighMonth = globalConfig.getHighMonths().getProportions();
        return proportionHighMonth;
    }

    /**
     * 高峰期日期倍数
     *
     * @return
     */
    public double proportionHighDay() {
        double proportionHighDay = globalConfig.getHighDays().getProportions();
        return proportionHighDay;
    }

    /**
     * 高峰期小时倍数
     *
     * @return
     */
    public double proportionHighHours() {
        double proportionHighHours = globalConfig.getHighHours().getProportions();
        return proportionHighHours;
    }

    /**
     * 低峰期月份倍数
     *
     * @return
     */
    public double proportionLowMonth() {
        double proportionLowMonth = globalConfig.getLowMonths().getProportions();
        return proportionLowMonth;
    }

    /**
     * 低峰期日期倍数
     *
     * @return
     */
    public double proportionLowDay() {
        double proportionLowDay = globalConfig.getLowDays().getProportions();
        return proportionLowDay;
    }

    /**
     * 低峰期小时倍数
     *
     * @return
     */
    public double proportionLowHours() {
        double proportionLowHours = globalConfig.getLowHours().getProportions();
        return proportionLowHours;
    }



}
