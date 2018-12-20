package com.wd.cloud.commons.util;

/**
 * @author He Zhigang
 * @date 2018/8/13
 * @Description:
 */
public class IpUtil {

    public static Long ipToLong(String ip) {
        long ips = 0L;
        String[] numbers = ip.split("\\.");
        //等价上面
        for (int i = 0; i < 4; ++i) {
            ips = ips << 8 | Integer.parseInt(numbers[i]);
        }
        return ips;
    }

    public static String longToIp(Long number) {
        //等价上面
        String ip = "";
        for (int i = 3; i >= 0; i--) {
            ip += String.valueOf((number & 0xff));
            if (i != 0) {
                ip += ".";
            }
            number = number >> 8;
        }

        return ip;
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
        if (ipToLong(beginIp) < ipToLong(endIp)){
            return ipToLong(ip) >= ipToLong(beginIp) && ipToLong(ip) <= ipToLong(endIp);
        }else{
            return ipToLong(ip) >= ipToLong(endIp) && ipToLong(ip) <= ipToLong(beginIp);
        }
    }

}
