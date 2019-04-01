package com.wd.cloud.commons.util;

/**
 * @author He Zhigang
 * @date 2018/8/13
 * @Description:
 */
public class NetUtil extends cn.hutool.core.util.NetUtil {

    public static long ipToLong(String ip) {
        final String[] numbers = ip.trim().split("\\.");
        return (Long.parseLong(numbers[0]) << 24)
                + (Long.parseLong(numbers[1]) << 16)
                + (Long.parseLong(numbers[2]) << 8)
                + (Long.parseLong(numbers[3]));
    }

    public static String longToIp(Long number) {
        return String.valueOf(number >> 24) +
                "." +
                ((number & 0x00FFFFFF) >> 16) +
                "." +
                ((number & 0x0000FFFF) >> 8) +
                "." +
                (number & 0x000000FF);
    }

    /**
     * ip是否在范围内
     *
     * @param ip
     * @param beginIp
     * @param endIp
     * @return
     */
    public static boolean isInner(String ip, String beginIp, String endIp) {
        if (ipToLong(beginIp) < ipToLong(endIp)) {
            return ipToLong(ip) >= ipToLong(beginIp) && ipToLong(ip) <= ipToLong(endIp);
        } else {
            return ipToLong(ip) >= ipToLong(endIp) && ipToLong(ip) <= ipToLong(beginIp);
        }
    }

}
