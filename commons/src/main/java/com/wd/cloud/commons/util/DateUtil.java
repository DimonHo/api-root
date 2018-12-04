package com.wd.cloud.commons.util;

import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/11/26
 * @Description:
 */
public class DateUtil extends cn.hutool.core.date.DateUtil {

    /**
     * 格式化mysql时间
     * @param type
     * @return
     */
    public static String formatMysqlStr(int type){
        String dateFormatStr = null;
        switch (type) {
            case 1:
                dateFormatStr = "%Y-%m-%d %H:00:00";
                break;
            case 2:
                dateFormatStr = "%Y-%m-%d 00:00:00";
                break;
            case 3:
                dateFormatStr = "%Y-%m-01 00:00:00";
                break;
            case 4:
                dateFormatStr = "%Y-01-01 00:00:00";
                break;
            default:
                dateFormatStr = "%Y-%m-%d %H:%i:00";
                break;
        }
        return dateFormatStr;
    }

    /**
     * 格式化时间
     * @param type
     * @return
     */
    public static String formatStr(int type){
        String dateFormatStr = null;
        switch (type) {
            case 1:
                dateFormatStr = "yyyy-MM-dd HH";
                break;
            case 2:
                dateFormatStr = "yyyy-MM-dd";
                break;
            case 3:
                dateFormatStr = "yyyy-MM";
                break;
            case 4:
                dateFormatStr = "yyyy";
                break;
            default:
                dateFormatStr = "yyyy-MM-dd HH:mm";
                break;
        }
        return dateFormatStr;
    }

    public List<String> range(String start,String end){
        return null;
    }
}
