package com.wd.cloud.commons.util;

import sun.net.util.IPAddressUtil;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

/**
 * @author He Zhigang
 * @date 2018/8/13
 * @Description:
 */
public class NetUtil extends cn.hutool.core.util.NetUtil {

    private final static Pattern IPV6_COMPRESS = Pattern.compile("(^|:)(0+(:|$)){2,8}");

    /**
     * 是否是合法IP, ipv4 or ipv6
     *
     * @param ip
     * @return
     */
    public static boolean isIp(String ip) {
        return IPAddressUtil.isIPv4LiteralAddress(ip) || IPAddressUtil.isIPv6LiteralAddress(ip);
    }

    /**
     * 数字转IP
     *
     * @param bigInteger
     * @param isIpV6     是否是IPV6地址
     * @return
     */
    public static String bigIntegerToIp(BigInteger bigInteger, boolean isIpV6) {
        if (isIpV6) {
            return bigIntegerToIpv6(bigInteger);
        } else {
            return bigIntegerToIpV4(bigInteger);
        }
    }

    public static String bigIntegerToIpV4(BigInteger bigInteger) {
        long number = bigInteger.longValue();
        return (number >> 24) +
                "." +
                ((number & 0x00FFFFFF) >> 16) +
                "." +
                ((number & 0x0000FFFF) >> 8) +
                "." +
                (number & 0x000000FF);
    }

    public static String bigIntegerToIpv6(BigInteger number) {
        return bigIntegerToIpv6(number, true);
    }

    public static String bigIntegerToIpv6(BigInteger number, boolean isCompress) {
        StringBuilder str = new StringBuilder();
        BigInteger ff = BigInteger.valueOf(0xffff);
        for (int i = 0; i < 8; i++) {
            str.insert(0, number.and(ff).toString(16) + ":");
            number = number.shiftRight(16);
        }
        str = new StringBuilder(str.substring(0, str.length() - 1));
        return isCompress ? IPV6_COMPRESS.matcher(str.toString()).replaceFirst("::") : str.toString();
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
        BigInteger ipNum = ipToBigInteger(ip);
        BigInteger beginIpNum = ipToBigInteger(beginIp);
        BigInteger endIpNum = ipToBigInteger(endIp);
        // 如果起始IP小于结束IP
        if (beginIpNum.compareTo(endIpNum) < 0) {
            return ipNum.compareTo(beginIpNum) >= 0 && ipNum.compareTo(endIpNum) <= 0;
            // 如果起始IP大于等于结束IP
        } else {
            return ipNum.compareTo(endIpNum) >= 0 && ipNum.compareTo(beginIpNum) <= 0;
        }

    }

    public static BigInteger ipToBigInteger(String ip) {
        BigInteger bigInteger = BigInteger.ZERO;
        // 转换为完整形式的ip字符串
        try {
            ip = InetAddress.getByName(ip).getHostAddress();
            // ipv4转数字
            if (IPAddressUtil.isIPv4LiteralAddress(ip)) {
                final long[] numbers = StrUtil.splitToLong(ip, ".");
                bigInteger = BigInteger.valueOf((numbers[0] << 24)
                        + (numbers[1] << 16)
                        + (numbers[2] << 8)
                        + (numbers[3]));
                //ipv6转数字
            } else if (IPAddressUtil.isIPv6LiteralAddress(ip)) {
                final String[] numbers = StrUtil.split(ip, ":");
                for (int i = 0; i < numbers.length; i++) {
                    bigInteger = bigInteger.add(
                            BigInteger.valueOf(Long.valueOf(numbers[i], 16)).shiftLeft(16 * (numbers.length - 1 - i))
                    );
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return bigInteger;
    }

}
