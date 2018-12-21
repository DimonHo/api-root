package com.wd.cloud.searchserver.service.impl;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.commons.util.DateUtil;
import com.wd.cloud.searchserver.repository.ContentAnalysisRepository;
import com.wd.cloud.searchserver.repository.DownloadInfoRepository;
import com.wd.cloud.searchserver.service.TjService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("tjService")
public class TjServiceImpl implements TjService {

    private static final Log log = LogFactory.get();
    @Autowired
    DownloadInfoRepository downloadInfoRepository;

    @Autowired
    ContentAnalysisRepository contentAnalysisRepository;

    @Override
    public Map<String, BigInteger> tjDcCount(String school, String date, Integer type) {
        String format = DateUtil.formatMysqlStr2(type);
        log.info("school={},date={},format={}", school, date, format);
        List<Map<String, Object>> result = school == null ? downloadInfoRepository.findAllSchoolDcCount(date, format)
                : downloadInfoRepository.findBySchoolDcCount(school, date, format);
        Map<String, BigInteger> dcResult = new HashMap<>();
        result.forEach(rs -> {
            String orgName = rs.get("orgName") != null ? rs.get("orgName").toString() : "null";
            dcResult.put(orgName, (BigInteger) rs.get("dcCount"));
        });
        return dcResult;
    }

    @Override
    public Map<String, BigInteger> tjScCount(String school, String date, Integer type) {
        String format = DateUtil.formatMysqlStr2(type);
        log.info("school={},date={},format={}", school, date, format);
        List<Map<String, Object>> result = school == null ? contentAnalysisRepository.findAllSchoolScCount(date, format)
                : contentAnalysisRepository.findBySchoolScCount(school, date, format);
        Map<String, BigInteger> dcResult = new HashMap<>();
        result.forEach(rs -> {
            String orgName = rs.get("orgName") != null ? rs.get("orgName").toString() : "null";
            dcResult.put(orgName, (BigInteger) rs.get("scCount"));
        });
        return dcResult;
    }
}
