package com.wd.cloud.commons.util;

import cn.hutool.json.JSONObject;
import com.wd.cloud.commons.constant.SessionConstant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author He Zhigang
 * @date 2019/1/14
 * @Description:
 */
public class SessionUtil {

    public static HttpSession getLoginSession(HttpServletRequest request) {
        if ((boolean) request.getSession().getAttribute(SessionConstant.KICKOUT)) {
            return null;
        } else {
            return request.getSession();
        }
    }

    public static JSONObject getLoginUserInfo(HttpServletRequest request) {
        HttpSession session = getLoginSession(request);
        JSONObject userInfo = null;
        if (session != null) {
            userInfo = (JSONObject) session.getAttribute(SessionConstant.LOGIN_USER);
        }
        return userInfo;
    }
}
