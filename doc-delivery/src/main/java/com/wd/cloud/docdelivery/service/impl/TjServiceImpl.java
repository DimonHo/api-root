package com.wd.cloud.docdelivery.service.impl;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.commons.util.DateUtil;
import com.wd.cloud.docdelivery.repository.HelpRecordRepository;
import com.wd.cloud.docdelivery.service.TjService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author He Zhigang
 * @date 2018/12/18
 * @Description:
 */
@Service("tjService")
public class TjServiceImpl implements TjService {

    private static final Log log = LogFactory.get();
    @Autowired
    HelpRecordRepository helpRecordRepository;

    @Override
    public Map<String, BigInteger> ddcCount(String orgName, String date, int type) {
        String format = DateUtil.formatMysqlDate(type);
        log.info("orgName={},date={},format={}", orgName, date, format);
        // 如果orgName为空，则查询所有的机构统计
        List<Map<String, Object>> result = orgName != null ? helpRecordRepository.findByOrgNameDdcCount(orgName, date, format)
                : helpRecordRepository.findAllDdcCount(date, format);
        Map<String, BigInteger> ddcCountMap = new HashMap<>();
        result.forEach(rs -> {
            String name = rs.get("orgName") != null ? rs.get("orgName").toString() : "null";
            ddcCountMap.put(name, (BigInteger) rs.get("ddcCount"));
        });
        return ddcCountMap;
    }
}
