package com.wd.cloud.docdelivery.service;

import java.math.BigInteger;
import java.util.Map;

/**
 * @author He Zhigang
 * @date 2018/12/18
 * @Description:
 */
public interface TjService {

    Map<String, BigInteger> ddcCount(String orgName, String date, int type);
}
