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
import com.wd.cloud.wdtjserver.utils.DateUtils;
import com.wd.cloud.wdtjserver.utils.FindDates;
import com.wd.cloud.wdtjserver.utils.TimeUtils;
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
    public  List<Map<String,Object>> findByTjDateAndOrgIdTime(Date beginDate, Date endDate, long orgId) {
        Map<String, Object> date = getDate(beginDate, endDate);

        List<Map<String,Object>> list = tjViewDataRepository.findByTjDateAndOrgIdTime(date.get("sqlDate_begin").toString(), date.get("sqlDate_end").toString(), orgId);
        return list;
    }

    @Override
    public List<Map<String,Object>> findByTjDateAndOrgIdDay(Date beginDate, Date endDate, long orgId) {
        Map<String, Object> date = getDate(beginDate, endDate);
        List<Map<String,Object>> list = tjViewDataRepository.findByTjDateAndOrgIdDay(date.get("sqlDate_begin").toString(), date.get("sqlDate_end").toString(), orgId);
        return list;
    }

    @Override
    public List<Map<String,Object>> findByTjDateAndOrgIdMonth(Date beginDate, Date endDate, long orgId) {
        Map<String, Object> date = getDate(beginDate, endDate);
        List<Map<String,Object>> list = tjViewDataRepository.findByTjDateAndOrgIdMonth(date.get("sqlDate_begin").toString(),date.get("sqlDate_end").toString(),orgId);
        return list;
    }

    @Override
    public List<Map<String,Object>> findByTjDateAndOrgIdYear(Date beginDate, Date endDate, long orgId) {
        Map<String, Object> date = getDate(beginDate, endDate);
        List<Map<String,Object>> list  = tjViewDataRepository.findByTjDateAndOrgIdYear(date.get("sqlDate_begin").toString(),date.get("sqlDate_end").toString(),orgId);
        return list;
    }

    @Override
    public List<TjOrg> findByBoole(boolean showPv, boolean showSc, boolean showDc, boolean showDdc, boolean showAvgTime) {
        List<TjOrg> list= tjOrgRepository.findByShowPvAndShowScAndShowDcAndShowDdcAndShowAvgTime(showPv, showSc, showDc, showDdc, showAvgTime);
        return list;
    }

    @Override
    public void search(TjHisSetting tjHisSetting) {
        //访问量
        int pvCount = tjHisSetting.getPvCount();
        //搜索量
        int scCount = tjHisSetting.getScCount();
        //下载量
        int dcCount = tjHisSetting.getDcCount();
        //文献传递量
        int ddcCount = tjHisSetting.getDdcCount();
        //平均访问时长
        Time avgTime = tjHisSetting.getAvgTime();

        //传入开始时间和结束时间间隔
        long between = DateUtil.between(tjHisSetting.getBeginTime(), tjHisSetting.getEndTime(), DateUnit.HOUR);
        //高峰月份
        int[] optionsHighMonth = globalConfig.getHighMonths().getOptions();
        double proportionHigh = globalConfig.getHighMonths().getProportions();
        //低峰月份
        int[] optionsLowMonth = globalConfig.getLowMonths().getOptions();
        double proportionLow = globalConfig.getLowMonths().getProportions();
        //
        Map<String, Object> map = TimeUtils.get(tjHisSetting.getEndTime(), tjHisSetting.getBeginTime());
        System.out.println("ggggggggggggggggggggggggggggg"+map);
        Integer result = Integer.parseInt( map.get("result").toString());
        //计算相隔时间间隔每天的量
        long avrDay=pvCount/between;
        //高峰日
        int[] optionsHighDay = globalConfig.getHighDays().getOptions();
        double proportionHighDay = globalConfig.getHighDays().getProportions();
        //低峰日
        int[] optionsLowDay = globalConfig.getLowDays().getOptions();
        double proportionLowDay = globalConfig.getLowDays().getProportions();
        //高峰时
        int[] optionsHighHour = globalConfig.getHighHours().getOptions();
        double proportionHighHour = globalConfig.getHighHours().getProportions();
        //低峰时
        int[] optionsLowHour = globalConfig.getLowHours().getOptions();
        double proportionLowHour = globalConfig.getLowHours().getProportions();
        for(int i=0;i<result;i++){
            long monthDay=Integer.parseInt(map.get("day"+i).toString());
            Integer month=Integer.parseInt(map.get("month"+i).toString());
            DateTime endMonth = DateUtil.endOfMonth(tjHisSetting.getBeginTime());
            List<String> strings=new ArrayList<>();
            try {
               strings = FindDates.get(tjHisSetting.getBeginTime().toString(), endMonth.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            Integer day=0;
            for(String s:strings){
                Integer hour=0;
               day=Integer.parseInt(s.substring(8,10));
               for(int h=0;h<23;h++){
                   hour=h;
                   //获得高峰月份
                   for(int j=0;j<optionsHighMonth.length;j++){
                       int dayMonth=optionsHighMonth[j];
                       //默认情况下一个月多少量
                       double monthCountNormal=avrDay * monthDay;
                       if(month==dayMonth){
                           //高峰月的量
                           monthCountNormal=monthCountNormal*proportionHigh;
                           getDay(optionsHighDay, proportionHighDay, optionsLowDay, proportionLowDay, optionsHighHour, proportionHighHour, optionsLowHour, proportionLowHour, monthDay, day, hour, monthCountNormal);
                       }
                   }
                   //获得低峰月份
                   for(int k=0;k<optionsLowMonth.length;k++){
                       int dayMonth=optionsLowMonth[k];
                       //默认情况下一个月多少量
                       double monthCountNormal=avrDay * monthDay;
                       if(month==dayMonth){
                           monthCountNormal=monthCountNormal*proportionLow;
                           getDay(optionsHighDay, proportionHighDay, optionsLowDay, proportionLowDay, optionsHighHour, proportionHighHour, optionsLowHour, proportionLowHour, monthDay, day, hour, monthCountNormal);
                       }
                   }
               }

            }

        }
    }

    private void getDay(int[] optionsHighDay, double proportionHighDay, int[] optionsLowDay, double proportionLowDay, int[] optionsHighHour, double proportionHighHour, int[] optionsLowHour, double proportionLowHour, long monthDay, Integer day, Integer hour, double monthCountNormal) {
        //默认一天多少量
        double dayCountNormal=monthCountNormal/monthDay;
        //高峰日
        for(int d=0;d<optionsHighDay.length;d++){
            int dayDay=optionsHighDay[d];
            //高峰日
            if(day==dayDay){
                dayCountNormal=dayCountNormal*proportionHighDay;
                getHours(optionsHighHour, proportionHighHour, optionsLowHour, proportionLowHour, hour, dayCountNormal);
            }
        }
        //低峰日
        for(int d=0;d<optionsLowDay.length;d++){
            int dayDay=optionsLowDay[d];
            //低峰日
            if(day==dayDay){
                dayCountNormal=dayCountNormal*proportionLowDay;
                getHours(optionsHighHour, proportionHighHour, optionsLowHour, proportionLowHour, hour, dayCountNormal);
            }
        }
    }

    private void getHours(int[] optionsHighHour, double proportionHighHour, int[] optionsLowHour, double proportionLowHour, Integer hour, double dayCountNormal) {
        //默认一小时的量
        double hourCountNormal=dayCountNormal/24;
        //高峰时
        for(int h=0;h<optionsHighHour.length;h++){
            int hourDay=optionsHighHour[h];
            if(hour==hourDay){
                hourCountNormal=hourCountNormal*proportionHighHour;
            }
        }
        //低峰时
        for(int h=0;h<optionsLowHour.length;h++){
            int hourDay=optionsLowHour[h];
            if(hour==hourDay){
                hourCountNormal=hourCountNormal*proportionLowHour;
            }
        }
    }

    public Map<String,Object> getDate(Date beginDate, Date endDate){
        java.util.Date utilDate_begin=new Date(beginDate+"");
        java.util.Date utilDate_end=new Date(endDate+"");
        java.sql.Date sqlDate_begin=new java.sql.Date(utilDate_begin.getTime());
        java.sql.Date sqlDate_end=new java.sql.Date(utilDate_end.getTime());
        Map<String,Object> map=new HashMap<>();
        map.put("sqlDate_begin",sqlDate_begin);
        map.put("sqlDate_end",sqlDate_end);
        return map;
    }

    public List<TjDaySetting> findByHistoryIsFalse() {
        /**
         * 查询History为false的数据
         */
        List<TjDaySetting> byHistoryIsFalse = tjDaySettingRepository.findByHistoryIsFalse();
        //获取第二天的时间
        String date = DateUtils.getNextDay(new Date().toString());
        Integer dateDay = Integer.parseInt(date.substring(8, 10));
        //获取本月
        Integer dateMonth = Integer.parseInt(date.substring(5, 7));
        for (TjDaySetting listMap : byHistoryIsFalse) {
            TjDaySetting tjDaySetting = listMap;
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
