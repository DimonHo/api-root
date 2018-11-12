package com.wd.cloud.wdtjserver.service.impl;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.wdtjserver.config.GlobalConfig;
import com.wd.cloud.wdtjserver.entity.TjDaySetting;
import com.wd.cloud.wdtjserver.entity.TjHisSetting;
import com.wd.cloud.wdtjserver.entity.TjOrg;
import com.wd.cloud.wdtjserver.repository.*;
import com.wd.cloud.wdtjserver.service.TjService;
import io.micrometer.core.instrument.util.TimeUtils;
import org.apache.commons.lang.time.DateUtils;
import org.bouncycastle.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;

import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
@Service("tjService")
public class TjServiceImpl implements TjService {
    private static final Log log = LogFactory.get();

    @Autowired
    TjDaySettingRepository tjDaySettingRepository;

    @Autowired
    TjOrgRepository tjOrgRepository;

    @Autowired
    TjHisSettingRepository tjHisSettingRepository;

    @Autowired
    TjViewDataRepository tjViewDataRepository;

    @Autowired
    GlobalConfig globalConfig;

    @Autowired
    TjTaskDataRepository tjTaskDataRepository;

    @Override
    public List<TjOrg> likeOrgName(String orgName) {
        List<TjOrg> list = tjOrgRepository.findByOrgNameLike("%"+orgName+"%");
        return list;
    }

    @Override
    public TjOrg save(TjOrg tjOrg) {
        //根据学校ID查询是否有该学校
        TjOrg oldTjOrg = tjOrgRepository.findByOrgIdAndHistoryIsFalse(tjOrg.getOrgId());
        if (oldTjOrg == null) {
            tjOrg = tjOrgRepository.save(tjOrg);
            log.info(tjOrg.toString());
        } else {
            //修改History为true
            oldTjOrg.setHistory(true);
            tjOrgRepository.save(oldTjOrg);
            //根据history的值拿到对应的pid
            tjOrg.setPid(oldTjOrg.getId());
            tjOrg = tjOrgRepository.save(tjOrg);
        }
        return tjOrg;
    }

    @Override
    public TjDaySetting save(TjDaySetting tjDaySetting) {
        //根据学校ID查询TjDaySetting是否有数据
        TjDaySetting oldTjDaySetting = tjDaySettingRepository.findByOrgIdAndHistoryIsFalse(tjDaySetting.getOrgId());
        if (oldTjDaySetting == null) {
            //新增一条数据到TjDaySetting表
            tjDaySetting = tjDaySettingRepository.save(tjDaySetting);
        } else {
            oldTjDaySetting.setHistory(true);
            tjDaySettingRepository.save(oldTjDaySetting);
            //根据history的值拿到对应的pid
            tjDaySetting.setPid(oldTjDaySetting.getId());
            tjDaySetting = tjDaySettingRepository.save(tjDaySetting);
        }
        return tjDaySetting;
    }

    @Override
    public TjHisSetting save(TjHisSetting tjHisSetting) {
        List<TjHisSetting> old_tjHisSettings = tjHisSettingRepository.findByOrgId(tjHisSetting.getOrgId());
        if (old_tjHisSettings.size() > 0) {
            boolean is = true;
            for (TjHisSetting old_tjHis : old_tjHisSettings) {
                is = DateUtil.isIn(tjHisSetting.getBeginTime(), old_tjHis.getBeginTime(), old_tjHis.getEndTime()) && old_tjHis.isLocked() ? false : true;
                is = DateUtil.isIn(tjHisSetting.getEndTime(), old_tjHis.getBeginTime(), old_tjHis.getEndTime()) && old_tjHis.isLocked() ? false : true;
            }
            if (is == true) {
                tjHisSettingRepository.save(tjHisSetting);
            } else {
                log.info("时间段重合了，请重新输入时间段");
            }
        } else {
            tjHisSettingRepository.save(tjHisSetting);
        }
        return tjHisSetting;
    }

    @Override
    public List<Map<String, Object>> findByTjDateAndOrgIdTime(Date beginDate, Date endDate, long orgId) {
        return null;
    }

    @Override
    public List<Map<String, Object>> findByTjDateAndOrgIdDay(Date beginDate, Date endDate, long orgId) {
        return null;
    }

    @Override
    public List<Map<String, Object>> findByTjDateAndOrgIdMonth(Date beginDate, Date endDate, long orgId) {
        return null;
    }

    @Override
    public List<Map<String, Object>> findByTjDateAndOrgIdYear(Date beginDate, Date endDate, long orgId) {
        return null;
    }

    @Override
    public List<TjDaySetting> findByHistoryIsFalse() {
        return null;
    }


    @Override
    public List<TjOrg> findByBoole(boolean showPv, boolean showSc, boolean showDc, boolean showDdc, boolean showAvgTime) {
        List<TjOrg> list= tjOrgRepository.findByShowPvAndShowScAndShowDcAndShowDdcAndShowAvgTime(showPv, showSc, showDc, showDdc, showAvgTime);
        return list;
    }

    @Override
    public void search(TjHisSetting tjHisSetting) {

    }



}
