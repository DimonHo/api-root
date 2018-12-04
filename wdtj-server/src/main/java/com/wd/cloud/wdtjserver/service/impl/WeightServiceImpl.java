package com.wd.cloud.wdtjserver.service.impl;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.wdtjserver.repository.TjWeightRepository;
import com.wd.cloud.wdtjserver.service.WeightService;
import com.wd.cloud.wdtjserver.utils.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author He Zhigang
 * @date 2018/12/4
 * @Description:
 */
@Service("weightService")
public class WeightServiceImpl implements WeightService {

    private static final Log log = LogFactory.get();

    @Autowired
    TjWeightRepository tjWeightRepository;

    @Override
    public Map<String, Double> buildWeightMap() {
        Map<String, Double> weightMap = new HashMap<>();
        tjWeightRepository.findAll().forEach(tjWeight -> {
            if (tjWeight.getLow() > tjWeight.getHigh()) {
                log.error("lowWeight:{} 必须小于highWeight:{},请检查数据表[tj_weight]的数据", tjWeight.getLow(), tjWeight.getHigh());
                throw new IllegalArgumentException("权重配置错误！");
            }
            // 在最低和最高权重之间随机一个权重
            double weight = RandomUtil.randomDouble(tjWeight.getLow(), tjWeight.getHigh());
            weightMap.put(tjWeight.getDateType() + "-" + tjWeight.getDateIndex(), weight);
        });
        return weightMap;
    }
}
