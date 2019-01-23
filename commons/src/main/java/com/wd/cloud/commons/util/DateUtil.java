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
    @Deprecated
    public static String formatMysqlStr(int type){
        String dateFormatStr = null;
        switch (type) {
            case 1:
                dateFormatStr = "%Y-%m-%d %H";
                break;
            case 2:
                dateFormatStr = "%Y-%m-%d";
                break;
            case 3:
                dateFormatStr = "%Y-%m";
                break;
            case 4:
                dateFormatStr = "%Y";
                break;
            default:
                dateFormatStr = "%Y-%m-%d %H:%i";
                break;
        }
        return dateFormatStr;
    }

    /**
     * 格式化mysql时间
     * @param type
     * @return
     */
    public static String formatMysqlStr2(int type){
        String dateFormatStr = null;
        switch (type) {
            case 1:
                dateFormatStr = "%Y-%m-%d %H:%i";
                break;
            case 2:
                dateFormatStr = "%Y-%m-%d %H:";
                break;
            case 3:
                dateFormatStr = "%Y-%m-%d";
                break;
            case 4:
                dateFormatStr = "%Y-%m";
                break;
            case 5:
                dateFormatStr = "%Y";
                break;
            default:
                dateFormatStr = "%Y-%m-%d %H:%i:%s";
                break;
        }
        return dateFormatStr;
    }


    /**
     * 格式化mysql时间
     * @param type
     * @return
     */
    public static String formatStr2(int type){
        String dateFormatStr = null;
        switch (type) {
            case 1:
                dateFormatStr = "yyyy-MM-dd HH:mm";
                break;
            case 2:
                dateFormatStr = "yyyy-MM-dd HH";
                break;
            case 3:
                dateFormatStr = "yyyy-MM-dd";
                break;
            case 4:
                dateFormatStr = "yyyy-MM";
                break;
            case 5:
                dateFormatStr = "yyyy";
                break;
            default:
                dateFormatStr = "yyyy-MM-dd HH:mm:ss";
                break;
        }
        return dateFormatStr;
    }

    /**
     * 格式化时间
     * @param type
     * @return
     */
    @Deprecated
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
