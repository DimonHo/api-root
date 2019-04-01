package com.wd.cloud.uoserver.service;

import java.util.List;
import java.util.Map;

/**
 * @Author: He Zhigang
 * @Date: 2019/3/20 18:20
 * @Description:
 */
public interface TjService {

    /**
     * 机构用户信息统计
     * @return
     */
    List<Map<String,Object>> tjOrgUser();

    /**
     * 院系统计
     * @param orgFlag
     * @return
     */
    List<Map<String,Object>> tjOrgDeptUser(String orgFlag);
}
