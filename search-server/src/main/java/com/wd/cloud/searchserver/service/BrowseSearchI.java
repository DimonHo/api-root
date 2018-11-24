package com.wd.cloud.searchserver.service;

import java.util.Map;

/**
 * @author He Zhigang
 * @date 2018/11/12 0012
 * @Description:
 */
public interface BrowseSearchI {
    public Map<String, Object> indexInfo(String school, String beginTime, String endTime);
}
