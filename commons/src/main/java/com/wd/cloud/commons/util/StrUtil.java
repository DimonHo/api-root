package com.wd.cloud.commons.util;

/**
 * @author He Zhigang
 * @date 2019/2/27
 * @Description:
 */
public class StrUtil extends cn.hutool.core.util.StrUtil {

    /**
     * 隐藏邮箱中间部分字符串
     *
     * @param email
     * @return
     */
    public static String hideMailAddr(String email) {
        String hideMail = email;
        if (email.contains("@")) {
            hideMail = email.replaceAll("(\\w?)(\\w+)(\\w)(@\\w+\\.[a-z]+(\\.[a-z]+)?)", "$1****$3$4");
        }
        return hideMail;
    }
}
