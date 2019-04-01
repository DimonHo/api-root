package com.wd.cloud.docdelivery.service;

import com.wd.cloud.docdelivery.pojo.dto.MyTjDTO;

import java.math.BigInteger;
import java.util.Map;

/**
 * @author He Zhigang
 * @date 2018/12/18
 * @Description:
 */
public interface TjService {

    /**
     * 实时文献传递量
     * @param orgName
     * @param date
     * @param type
     * @return
     */
    Map<String, BigInteger> ddcCount(String orgName, String date, int type);


    /**
     * 平台中求助总量
     *
     * @return
     */
    long totalForHelp();

    /**
     * 平台传递成功数量
     *
     * @return
     */
    long successTotal();

    /**
     * 平台今日已求助量
     *
     * @return
     */
    long todayTotalForHelp();

    /**
     * 今日求助成功的数量
     * @return
     */
    long successTodayTotalForHelp();

    /**
     * 平均响应时间
     * @param startDate
     * @return
     */
    long avgResponseTime(String startDate);

    /**
     * 成功的平均响应时间
     * @param startDate
     * @return
     */
    long avgSuccessResponseTime(String startDate);

    /**
     * 我的统计
     * @param username
     * @return
     */
    MyTjDTO tjUser(String username);


    /**
     * 邮箱的统计
     * @param email
     * @param ip
     * @return
     */
    MyTjDTO tjEmail(String email, String ip);


}
