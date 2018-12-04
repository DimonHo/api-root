package com.wd.cloud.wdtjserver.service;

import java.util.Map;

/**
 * @author He Zhigang
 * @date 2018/12/4
 * @Description:
 */
public interface WeightService {

    /**
     * 获取所有权重设置，组装map
     * @return
     */
    Map<String,Double> buildWeightMap();
}
