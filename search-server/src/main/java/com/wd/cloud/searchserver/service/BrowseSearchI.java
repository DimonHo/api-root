package com.wd.cloud.searchserver.service;

import java.util.Map;

/**
 * @author He Zhigang
 * @date 2018/11/12 0012
 * @Description:
 */
public interface BrowseSearchI {
    Map<String, Object> indexInfo(String school, String beginTime, String endTime);

    Map<String, Map<String, Integer>> tjData(String orgName, String beginDate, String endDate);
}
