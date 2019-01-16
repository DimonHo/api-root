package com.wd.cloud.docdelivery.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.commons.dto.OrgDTO;
import com.wd.cloud.commons.dto.UserDTO;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.commons.util.DateUtil;
import com.wd.cloud.docdelivery.dto.MyTjDTO;
import com.wd.cloud.docdelivery.entity.Permission;
import com.wd.cloud.docdelivery.enums.HelpStatusEnum;
import com.wd.cloud.docdelivery.feign.OrgServerApi;
import com.wd.cloud.docdelivery.repository.GiveRecordRepository;
import com.wd.cloud.docdelivery.repository.HelpRecordRepository;
import com.wd.cloud.docdelivery.repository.PermissionRepository;
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

    @Autowired
    GiveRecordRepository giveRecordRepository;

    @Autowired
    PermissionRepository permissionRepository;

    @Autowired
    OrgServerApi orgServerApi;

    @Override
    public Map<String, BigInteger> ddcCount(String orgName, String date, int type) {
        String format = DateUtil.formatMysqlStr2(type);
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

    @Override
    public long totalForHelp() {
        return helpRecordRepository.count();
    }

    @Override
    public long successTotal() {
        return helpRecordRepository.countByStatus(HelpStatusEnum.HELP_SUCCESSED.getValue());
    }

    @Override
    public long todayTotalForHelp() {
        return helpRecordRepository.todayTotal();
    }

    @Override
    public MyTjDTO tjUser(UserDTO userDTO, OrgDTO ipOrg) {
        Integer level = 0;
        if (userDTO != null){

        }
        if (ipOrg != null){

        }
        
        return null;
    }


    @Override
    public MyTjDTO tjEmail(String email, String ip) {
        int rule = 0;
        log.info("用户IP: {}", ip);
        ResponseModel<JSONObject> orgResponse = orgServerApi.getByIp(ip);
        log.info("机构返回信息: {}", orgResponse.toString());
        JSONObject orgInfo = null;
        if (!orgResponse.isError()) {
            orgInfo = orgResponse.getBody();
            rule += 1;
        }
        Permission permission = orgInfo == null ? permissionRepository.findByOrgIdIsNullAndLevel(rule) : permissionRepository.findByOrgIdAndLevel(orgInfo.getLong("id"), rule);
        if (permission == null) {
            permission = permissionRepository.findByOrgIdIsNullAndLevel(rule);
        }
        //今日已求助数量
        long myTodayHelpCount = helpRecordRepository.myTodayTotal(email);
        //我的总求助数量
        long myHelpCount = helpRecordRepository.countByHelperEmail(email);
        long successHelpCount = helpRecordRepository.countByHelperEmailAndStatus(email, HelpStatusEnum.HELP_SUCCESSED.getValue());
        //总上限
        Long total = permission.getTotal();
        //每日上限
        Long todayTotal = permission.getTodayTotal();
        //总剩余
        Long restTotal = total == null ? null : total - myHelpCount;
        if (restTotal != null && restTotal < 0) {
            restTotal = 0L;
        }
        // 今日剩余
        Long todayRestTotal = todayTotal == null ? null : todayTotal - myTodayHelpCount;
        //如果今日剩余量大于总剩余量，则今日最多还能求助总剩余数量个
        if (todayRestTotal != null && todayRestTotal < 0) {
            todayRestTotal = 0L;
        }
        todayRestTotal = (todayRestTotal != null && restTotal != null && todayRestTotal > restTotal) ? restTotal : todayRestTotal;
        MyTjDTO myTjDTO = new MyTjDTO();
        myTjDTO.setTotal(total)
                .setTodayTotal(todayTotal)
                .setHelpCount(myHelpCount)
                .setTodayHelpCount(myTodayHelpCount)
                .setRestTotal(restTotal)
                .setTodayRestTotal(todayRestTotal)
                .setSuccessHelpCount(successHelpCount);
        return myTjDTO;
    }
}
