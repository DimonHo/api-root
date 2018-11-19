package com.wd.cloud.searchserver.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * @author He Zhigang
 * @date 2018/11/12 0012
 * @Description:
 */
public interface FlowAnalysisServiceI {
    public List<Map<String, Object>> visite(Long orgId, String tjDate);

}
