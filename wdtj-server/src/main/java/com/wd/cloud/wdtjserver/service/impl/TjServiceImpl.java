package com.wd.cloud.wdtjserver.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.apifeign.OrgServerApi;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.wdtjserver.entity.TjDaySetting;
import com.wd.cloud.wdtjserver.entity.TjHisSetting;
import com.wd.cloud.wdtjserver.entity.TjOrg;
import com.wd.cloud.wdtjserver.entity.TjViewData;
import com.wd.cloud.wdtjserver.model.DateIntervalModel;
import com.wd.cloud.wdtjserver.model.HisQuotaModel;
import com.wd.cloud.wdtjserver.repository.*;
import com.wd.cloud.wdtjserver.service.TjService;
import com.wd.cloud.wdtjserver.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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
    TjDateSettingRepository tjDateSettingRepository;

    @Autowired
    TjTaskDataRepository tjTaskDataRepository;

    @Autowired
    OrgServerApi orgServerApi;

    @Override
    public TjOrg save(TjOrg tjOrg) {
        ResponseModel responseModel = orgServerApi.getOrg(tjOrg.getOrgId());
        if (!responseModel.isError()){
            //根据学校ID查询是否有该学校
            TjOrg oldTjOrg = tjOrgRepository.findByOrgIdAndHistoryIsFalse(tjOrg.getOrgId());
            if (oldTjOrg != null) {
                //修改History为true
                oldTjOrg.setHistory(true);
                tjOrg.setPid(oldTjOrg.getId());
                tjOrgRepository.save(oldTjOrg);
            }
            tjOrg.setOrgName(JSONUtil.parseObj(responseModel.getBody(),true).getStr("name"));
            return tjOrgRepository.save(tjOrg);
        }
        return null;

    }

    @Override
    public List<TjOrg> likeOrgName(String orgName) {
        return tjOrgRepository.findByOrgNameLike("%" + orgName + "%");
    }

    @Override
    public List<TjOrg> getAll(String sortField) {
        if ("orgName".equals(sortField)) {
            tjOrgRepository.getAllOrderByOrgName();
        }
        return tjOrgRepository.findAll(Sort.by(sortField));
    }

    @Override
    public List<TjOrg> filterByQuota(boolean showPv, boolean showSc, boolean showDc, boolean showDdc, boolean showAvgTime) {
        return tjOrgRepository.findByShowPvAndShowScAndShowDcAndShowDdcAndShowAvgTime(showPv, showSc, showDc, showDdc, showAvgTime);
    }

    @Override
    public TjDaySetting save(TjDaySetting tjDaySetting) {
        //根据学校ID查询TjDaySetting是否有数据
        TjDaySetting oldTjDaySetting = tjDaySettingRepository.findByOrgIdAndHistoryIsFalse(tjDaySetting.getOrgId());
        if (oldTjDaySetting != null) {
            oldTjDaySetting.setHistory(true);
            tjDaySetting.setPid(oldTjDaySetting.getId());
            tjDaySettingRepository.save(oldTjDaySetting);
        }
        return tjDaySettingRepository.save(tjDaySetting);
    }

    @Override
    public TjHisSetting save(TjHisSetting tjHisSetting) {
        return tjHisSettingRepository.save(tjHisSetting);
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
    public void search(TjHisSetting tjHisSetting) {

    }

    @Override
    public List<DateIntervalModel> saveTjHisSettings(Long orgId, List<HisQuotaModel> hisQuotaModels) {
        List<TjHisSetting> tjHisSettingList = tjHisSettingRepository.findByOrgId(orgId);
        List<TjHisSetting> tjHisSettings = new ArrayList<>();
        // 返回重叠时间区间
        List<DateIntervalModel> overlaps = new ArrayList<>();
        hisQuotaModelsForEach:
        for (HisQuotaModel hisQuotaModel : hisQuotaModels) {
            // 遍历数据库已存在的数据检查是否有重叠时区
            for (TjHisSetting tjHis : tjHisSettingList) {
                DateIntervalModel dbInterval = new DateIntervalModel(tjHis.getBeginTime(), tjHis.getEndTime());
                DateIntervalModel viewInterval = new DateIntervalModel(hisQuotaModel.getBeginTime(), hisQuotaModel.getEndTime());
                DateIntervalModel overlap = DateUtil.overlapDate(dbInterval, viewInterval);
                if (overlap != null && tjHis.isLocked()) {
                    overlaps.add(overlap);
                    // 跳过外层循环
                    continue hisQuotaModelsForEach;
                }
            }
            // 如果没有时区重叠，将数据添加到待插入的数据列表中
            TjHisSetting tjHisSetting = new TjHisSetting();
            tjHisSetting.setAvgTime(hisQuotaModel.getAvgTime())
                    .setBeginTime(hisQuotaModel.getBeginTime())
                    .setEndTime(hisQuotaModel.getEndTime())
                    .setPvCount(hisQuotaModel.getPvCount())
                    .setScCount(hisQuotaModel.getScCount())
                    .setDcCount(hisQuotaModel.getDcCount())
                    .setDdcCount(hisQuotaModel.getDdcCount())
                    .setOrgId(orgId);
            tjViewDataRepository.saveAll(buildTjHisData(tjHisSetting));
            tjHisSettings.add(tjHisSetting);
        }
        // 如果没有重叠部分，则保存数据
        if (overlaps.size() == 0) {
            tjHisSettingRepository.saveAll(tjHisSettings);
        }
        return overlaps;
    }


    /**
     * 生成历史数据
     *
     * @param tjHisSetting
     * @return
     */
    @Override
    public List<TjViewData> buildTjHisData(TjHisSetting tjHisSetting) {
        Map<String, Float> settingMap = new TreeMap();
        // 获取所有比率设置，组装map
        tjDateSettingRepository.findAll().forEach(tjDateSetting -> {
            settingMap.put(tjDateSetting.getDateType() + "-" + tjDateSetting.getDateIndex(), tjDateSetting.getWeight());
        });
        // 获取分钟数列表
        List<DateTime> minuteList = DateUtil.rangeToList(tjHisSetting.getBeginTime(), tjHisSetting.getEndTime(), DateField.MINUTE);
        Map<DateTime, Float> minuteWeightMap = new TreeMap();
        //计算每分钟比率值放入map中
        minuteList.forEach(minuteTime -> {
            String monthKey = "1-" + (DateUtil.month(minuteTime) + 1);
            String hoursKey = "4-" + DateUtil.hour(minuteTime, true);
            float monthWeight = settingMap.get(monthKey) == null ? 1.0F : settingMap.get(monthKey);
            float hourWeight = settingMap.get(hoursKey) == null ? 1.0F : settingMap.get(hoursKey);
            float weight = monthWeight * hourWeight;
            minuteWeightMap.put(minuteTime, weight);
        });
        // 生成历史数据
        List<TjViewData> tjViewDataList = new ArrayList();

        // 各指标总量
        AtomicInteger sumPvCount = new AtomicInteger(tjHisSetting.getPvCount());
        AtomicInteger sumScCount = new AtomicInteger(tjHisSetting.getScCount());
        AtomicInteger sumDcCount = new AtomicInteger(tjHisSetting.getDcCount());
        AtomicInteger sumDdcCount = new AtomicInteger(tjHisSetting.getDdcCount());

        // 每分钟平均值
        float minutes = minuteList.size();
        float avgPvCountFromMinute = tjHisSetting.getPvCount() / minutes;
        float avgScCountFromMinute = tjHisSetting.getScCount() / minutes;
        float avgDcCountFromMinute = tjHisSetting.getDcCount() / minutes;
        float avgDdcCountFromMinute = tjHisSetting.getDdcCount() / minutes;
        //根据比率计算随机值
        minuteWeightMap.forEach((date, weight) -> {
            TjViewData tjViewData = new TjViewData();
            // 随机数量
            int pvRandomCount = (int) Math.round(RandomUtil.randomDouble(avgPvCountFromMinute * 2) * weight + RandomUtil.randomDouble(-1, 1));
            int scRandomCount = (int) Math.round(RandomUtil.randomDouble(avgScCountFromMinute * 2) * weight + RandomUtil.randomDouble(-1, 1));
            int dcRandomCount = (int) Math.round(RandomUtil.randomDouble(avgDcCountFromMinute * 2) * weight + RandomUtil.randomDouble(-1, 1));
            int ddcRandomCount = (int) Math.round(RandomUtil.randomDouble(avgDdcCountFromMinute * 2) * weight + RandomUtil.randomDouble(-1, 1));
            long avgRandomTime = Math.round(tjHisSetting.getAvgTime().getTime() * weight + RandomUtil.randomLong(-100000, 100000));

            // 实际数量
            int pvCount = pvRandomCount < 0 ? 0 : pvRandomCount;
            int scCount = scRandomCount < 0 ? 0 : scRandomCount;
            int dcCount = dcRandomCount < 0 ? 0 : dcRandomCount;
            int ddcCount = ddcRandomCount < 0 ? 0 : ddcRandomCount;

            if (pvCount < sumPvCount.get()) {
                tjViewData.setPvCount(pvCount);
                sumPvCount.set(sumPvCount.get() - pvCount);
            } else {
                tjViewData.setPvCount(sumPvCount.get());
                sumPvCount.set(0);
            }

            if (scCount < sumScCount.get()) {
                tjViewData.setScCount(scCount);
                sumScCount.set(sumScCount.get() - scCount);
            } else {
                tjViewData.setScCount(sumScCount.get());
                sumScCount.set(0);
            }

            if (dcCount < sumDcCount.get()) {
                tjViewData.setDcCount(dcCount);
                sumDcCount.set(sumDcCount.get() - dcCount);
            } else {
                tjViewData.setDcCount(sumDcCount.get());
                sumDcCount.set(0);
            }

            if (ddcCount < sumDdcCount.get()) {
                tjViewData.setDdcCount(ddcCount);
                sumDdcCount.set(sumDdcCount.get() - ddcCount);
            } else {
                tjViewData.setDdcCount(sumDdcCount.get());
                sumDdcCount.set(0);
            }

            tjViewData.setAvgTime(new Time(avgRandomTime));
            tjViewData.setOrgId(tjHisSetting.getOrgId());
            tjViewData.setTjDate(date.toTimestamp());
            tjViewDataList.add(tjViewData);
        });
        tjViewDataRepository.saveAll(tjViewDataList);
        return tjViewDataList;
    }
}
