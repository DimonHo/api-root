package com.wd.cloud.wdtjserver.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.wdtjserver.config.GlobalConfig;
import com.wd.cloud.wdtjserver.entity.TjDaySetting;
import com.wd.cloud.wdtjserver.entity.TjHisSetting;
import com.wd.cloud.wdtjserver.entity.TjOrg;
import com.wd.cloud.wdtjserver.repository.*;
import com.wd.cloud.wdtjserver.service.TjService;
import com.wd.cloud.wdtjserver.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
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
        List<TjOrg> list = tjOrgRepository.findByOrgNameLike("%" + orgName + "%");
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
        Map<String, Object> date = getDate(beginDate, endDate);

        List<Map<String, Object>> list = tjViewDataRepository.findByTjDateAndOrgIdTime(date.get("sqlDate_begin").toString(), date.get("sqlDate_end").toString(), orgId);
        return list;
    }

    @Override
    public List<Map<String, Object>> findByTjDateAndOrgIdDay(Date beginDate, Date endDate, long orgId) {
        Map<String, Object> date = getDate(beginDate, endDate);
        List<Map<String, Object>> list = tjViewDataRepository.findByTjDateAndOrgIdDay(date.get("sqlDate_begin").toString(), date.get("sqlDate_end").toString(), orgId);
        return list;
    }

    @Override
    public List<Map<String, Object>> findByTjDateAndOrgIdMonth(Date beginDate, Date endDate, long orgId) {
        Map<String, Object> date = getDate(beginDate, endDate);
        List<Map<String, Object>> list = tjViewDataRepository.findByTjDateAndOrgIdMonth(date.get("sqlDate_begin").toString(), date.get("sqlDate_end").toString(), orgId);
        return list;
    }

    @Override
    public List<Map<String, Object>> findByTjDateAndOrgIdYear(Date beginDate, Date endDate, long orgId) {
        Map<String, Object> date = getDate(beginDate, endDate);
        List<Map<String, Object>> list = tjViewDataRepository.findByTjDateAndOrgIdYear(date.get("sqlDate_begin").toString(), date.get("sqlDate_end").toString(), orgId);
        return list;
    }


    public Map<String, Object> getDate(Date beginDate, Date endDate) {
        java.util.Date utilDate_begin = new Date(beginDate + "");
        java.util.Date utilDate_end = new Date(endDate + "");
        java.sql.Date sqlDate_begin = new java.sql.Date(utilDate_begin.getTime());
        java.sql.Date sqlDate_end = new java.sql.Date(utilDate_end.getTime());
        Map<String, Object> map = new HashMap<>();
        map.put("sqlDate_begin", sqlDate_begin);
        map.put("sqlDate_end", sqlDate_end);
        return map;
    }

    @Override
    public List<TjDaySetting> findByHistoryIsFalse() {
        /**
         * 查询History为false的数据
         */
        List<TjDaySetting> byHistoryIsFalse = tjDaySettingRepository.findByHistoryIsFalse();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //获取当前时间
        String format = simpleDateFormat.toString();
        for (TjDaySetting listMap : byHistoryIsFalse) {
            TjDaySetting tjDaySetting = listMap;

            //获取当前时间的小时
            int datehour = DateUtil.hour(new Date(), true);
            //获取当天
            Integer dateDay = Integer.parseInt(format.substring(8, 10));
            //获取本月
            Integer dateMonth = Integer.parseInt(format.substring(5, 7));
            //学校ID
            long orgId = tjDaySetting.getOrgId();
            //下载量
            int dcCount = tjDaySetting.getDcCount();

            //文献传递量
            int ddcCount = tjDaySetting.getDdcCount();
            //访问量
            int pvCount = tjDaySetting.getPvCount();
            //搜索量
            int scCount = tjDaySetting.getScCount();

            //得到每小时平均分配多少条数据
            double usDcCount = dcCount / 24;
            double usDdcCount = ddcCount / 24;
            double usPvCount = pvCount / 24;
            double usScCount = scCount / 24;

            //获取第二天的时间
            String date = DateUtils.getNextDay(new Date().toString());


            int optionsHighMonth[] = globalConfig.getHighMonths().getOptions();
            //获取该天的小时数
            for (int h = 0; h < 24; h++) {
                for (int o = 0;o<=optionsHighMonth.length;o++){
                    if (optionsHighMonth[o] == h ){

                    }
                }

                //获取该小时的分钟数
                for (int m = 0;m<60;m++){

                }
            }


        }
        return byHistoryIsFalse;
    }


}
