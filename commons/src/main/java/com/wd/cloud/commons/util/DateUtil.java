package com.wd.cloud.commons.util;

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
}
