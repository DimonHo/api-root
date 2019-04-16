package com.wd.cloud.commons.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author He Zhigang
 * @date 2019/2/23
 * @Description:
 */
public class HttpUtil extends cn.hutool.http.HttpUtil {

    /**
     * 获取cookie键值对字符串
     *
     * @param request
     * @return
     */
    public static String getCookieStr(HttpServletRequest request) {
        StringBuilder cookieStr = new StringBuilder();
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookieStr.length() > 0) {
                    cookieStr.append(";");
                }
                cookieStr.append(cookie.getName()).append("=").append(cookie.getValue());
            }
        }
        return cookieStr.toString();
    }

    public static String getCookieValue(HttpServletRequest request, String cookieName) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
