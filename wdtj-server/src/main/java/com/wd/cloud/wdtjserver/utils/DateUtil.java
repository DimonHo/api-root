package com.wd.cloud.wdtjserver.utils;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import com.wd.cloud.wdtjserver.model.DateIntervalModel;
import org.apache.commons.lang.time.DateUtils;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * @author He Zhigang
 * @date 2018/11/10
 * @Description:
 */
public class DateUtil extends cn.hutool.core.date.DateUtil {


    public static long getTimeMillis(Time time) {
        return time.getTime() + TimeZone.getDefault().getRawOffset();
    }

    public static Time createTime(long time) {
        return new Time(time - TimeZone.getDefault().getRawOffset());
    }


    /**
     * 格式yyyy-MM-dd HH:mm
     *
     * @param dateString 标准形式的时间字符串
     * @return 日期对象
     */
    public static DateTime parseDateForMinute(String dateString) {
        dateString = normalize(dateString);
        return parse(dateString, DatePattern.NORM_DATETIME_MINUTE_FORMAT);
    }

    /**
     * 获取当前小时的所有分钟
     * @param dateStr
     * @return
     */
    public static List<DateTime> rangeMinuteFromHours(String dateStr) {
        Date date = DateUtil.parse(dateStr, "yyyy-MM-dd HH:00:00");
        return DateUtil.rangeToList(date, DateUtil.offsetHour(date, 1), DateField.MINUTE);
    }

    /**
     * 获取当前小时的所有分钟
     * @param date
     * @return
     */
    public static List<DateTime> rangeMinuteFromHours(Date date) {

        return DateUtil.rangeToList(date, DateUtil.offsetHour(date, 1), DateField.MINUTE);
    }

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


    /**
     * 标准化日期，默认处理以空格区分的日期时间格式，空格前为日期，空格后为时间：<br>
     * 将以下字符替换为"-"
     *
     * <pre>
     * "."
     * "/"
     * "年"
     * "月"
     * </pre>
     * <p>
     * 将以下字符去除
     *
     * <pre>
     * "日"
     * </pre>
     * <p>
     * 将以下字符替换为":"
     *
     * <pre>
     * "时"
     * "分"
     * "秒"
     * </pre>
     * <p>
     * 当末位是":"时去除之（不存在毫秒时）
     *
     * @param dateStr 日期时间字符串
     * @return 格式化后的日期字符串
     */
    private static String normalize(String dateStr) {
        if (StrUtil.isBlank(dateStr)) {
            return dateStr;
        }

        // 日期时间分开处理
        final List<String> dateAndTime = StrUtil.splitTrim(dateStr, ' ');
        final int size = dateAndTime.size();
        if (size < 1 || size > 2) {
            // 非可被标准处理的格式
            return dateStr;
        }

        final StringBuilder builder = StrUtil.builder();

        // 日期部分（"\"、"/"、"."、"年"、"月"都替换为"-"）
        String datePart = dateAndTime.get(0).replaceAll("[\\/.年月]", "-");
        datePart = StrUtil.removeSuffix(datePart, "日");
        builder.append(datePart);

        // 时间部分
        if (size == 2) {
            builder.append(' ');
            String timePart = dateAndTime.get(1).replaceAll("[时分秒]", ":");
            timePart = StrUtil.removeSuffix(timePart, ":");
            builder.append(timePart);
        }

        return builder.toString();
    }

}
