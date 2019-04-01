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

    long avgResponseTime(String startDate);

    long avgSuccessResponseTime(String startDate);

    /**
     * 我的统计
     *
     * @return
     */
    MyTjDTO tjUser(String username);


    MyTjDTO tjEmail(String email, String ip);


}
