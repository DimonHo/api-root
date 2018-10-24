package com.wd.cloud.searchserver.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

public class DoubleUtil {

    /**
     * double转换成保留两位小数
     *
     * @param value
     * @return
     */
    public static double format(double value) {
        if (Double.isNaN(value)) {
            value = 0;
        } else if (Double.isInfinite(value)) {
            value = 0;
            System.err.println("double is isInfinite!");
        }
        BigDecimal bPage = new BigDecimal(value);
        value = bPage.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return value;
    }

    /**
     * 将double时间类型转换成HH:mm:ss
     *
     * @param ms
     * @return
     */
    public static String longToStringForTime(double ms) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String hms = formatter.format(ms * 1000 - 8 * 3600 * 1000);
        return hms;
    }

}
