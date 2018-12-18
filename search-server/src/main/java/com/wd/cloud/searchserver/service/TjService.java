package com.wd.cloud.searchserver.service;

import java.math.BigInteger;
import java.util.Map;

public interface TjService {

    Map<String, BigInteger> tjDcCount(String school, String date, Integer type);
}
