package com.wd.cloud.wdtjserver.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.wdtjserver.entity.*;
import com.wd.cloud.wdtjserver.model.DateIntervalModel;
import com.wd.cloud.wdtjserver.model.HisQuotaModel;
import com.wd.cloud.wdtjserver.model.WeightModel;
import com.wd.cloud.wdtjserver.repository.*;
import com.wd.cloud.wdtjserver.service.TjService;
import com.wd.cloud.wdtjserver.utils.DateUtil;
import com.wd.cloud.wdtjserver.utils.JpaQueryUtil;
import com.wd.cloud.wdtjserver.utils.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
@Service("tjService")
@Transactional(rollbackFor = Exception.class)
public class TjServiceImpl implements TjService {
    private static final Log log = LogFactory.get();
    @Autowired
    TjQuotaRepository tjQuotaRepository;
    @Autowired
    TjOrgRepository tjOrgRepository;
    @Autowired
    TjHisQuotaRepository tjHisQuotaRepository;
    @Autowired
    TjViewDataRepository tjViewDataRepository;
    @Autowired
    TjDateSettingRepository tjDateSettingRepository;
    @Autowired
    TjTaskDataRepository tjTaskDataRepository;


    @Override
    public Page<TjOrg> likeOrgName(String orgName, Boolean history, Pageable pageable) {
        return tjOrgRepository.findAll(JpaQueryUtil.buildQeuryForTjOrg(orgName,history),pageable);
    }

    @Override
    public Page<TjOrg> getEnabledFromAll(Pageable pageable) {
        return tjOrgRepository.findAllByHistory(false, pageable);
    }

    @Override
    public Page<TjOrg> getHistoryFromAll(Pageable pageable) {

        return tjOrgRepository.findAllByHistory(true, pageable);
    }

    @Override
    public Page<TjOrg> getAll(Pageable pageable) {
        return tjOrgRepository.findAll(pageable);
    }

    @Override
    public Page<TjOrg> filterOrgByQuota(Boolean showPv, Boolean showSc, Boolean showDc, Boolean showDdc, Boolean showAvgTime, Boolean forbade, Pageable pageable) {
        return tjOrgRepository.findAll(JpaQueryUtil.buildFilterForTjOrg(showPv, showSc, showDc, showDdc, showAvgTime,forbade), pageable);
    }


    @Override
    public TjQuota findOrgQuota(Long orgId) {
        return tjQuotaRepository.findByOrgIdAndHistoryIsFalse(orgId);
    }

    @Override
    public Page<TjQuota> findOrgQuota(Long orgId, Boolean history, Pageable pageable) {
        if (history == null) {
            return tjQuotaRepository.findByOrgId(orgId, pageable);
        } else {
            return tjQuotaRepository.findByOrgIdAndHistory(orgId, history, pageable);
        }

    }

    @Override
    public Page<TjQuota> findAll(Boolean history, Pageable pageable) {
        if (history != null) {
            return tjQuotaRepository.findByHistory(history, pageable);
        }
        return tjQuotaRepository.findAll(pageable);
    }


    @Override
    public TjHisQuota getHisQuota(Long hisId) {
        return tjHisQuotaRepository.getOne(hisId);
    }

    @Override
    public Page<TjHisQuota> getHisQuotaByOrg(Long orgId, Pageable pageable) {
        return tjHisQuotaRepository.findByOrgId(orgId, pageable);
    }

    @Override
    public Page<TjHisQuota> getAllHisQuota(Pageable pageable) {
        return tjHisQuotaRepository.findAll(pageable);
    }


    @Override
    public Map<String, DateIntervalModel> checkInterval(Long orgId, List<HisQuotaModel> hisQuotaModels) {
        List<TjHisQuota> tjHisQuotaList = tjHisQuotaRepository.findByOrgId(orgId);
        // 返回重叠时间区间
        Map<String, DateIntervalModel> overlapMap = new HashMap<>();
        for (HisQuotaModel hisQuotaModel : hisQuotaModels) {
            // 遍历数据库已存在的数据检查是否有重叠时区
            for (TjHisQuota tjHis : tjHisQuotaList) {
                DateIntervalModel dbInterval = new DateIntervalModel(tjHis.getBeginTime(), tjHis.getEndTime());
                DateIntervalModel viewInterval = new DateIntervalModel(hisQuotaModel.getBeginTime(), hisQuotaModel.getEndTime());
                DateIntervalModel overlap = DateUtil.overlapDate(dbInterval, viewInterval);
                if (overlap != null && tjHis.isLocked()) {
                    String mapKey = String.format("%s - %s", DateUtil.formatDateTime(viewInterval.getBeginDate()), DateUtil.formatDateTime(viewInterval.getEndDate()));
                    overlapMap.put(mapKey, overlap);
                }
            }
        }
        return overlapMap;
    }


    /**
     * 生成历史数据
     *
     * @param tjHisQuota
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean buildTjHisData(TjHisQuota tjHisQuota) {
        Map<String, Float> settingMap = new TreeMap<>();
        // 获取所有比率设置，组装map
        tjDateSettingRepository.findAll().forEach(tjDateSetting -> {
            settingMap.put(tjDateSetting.getDateType() + "-" + tjDateSetting.getDateIndex(), tjDateSetting.getWeight());
        });
        // 获取分钟列表
        List<DateTime> minuteList = DateUtil.rangeToList(tjHisQuota.getBeginTime(), tjHisQuota.getEndTime(), DateField.MINUTE);
        Map<WeightModel, TjViewData> minuteWeightMap = new TreeMap<>();
        //计算每分钟比率值放入map中
        minuteList.forEach(minuteTime -> {
            WeightModel weightModel = new WeightModel();
            weightModel.setName(minuteTime.toString());
            String monthKey = "1-" + (DateUtil.month(minuteTime) + 1);
            String dayKey = "2-" + (DateUtil.dayOfMonth(minuteTime));
            // 周日-周六：1 - 7
            String weekKey = "3-" + (DateUtil.dayOfWeek(minuteTime));
            String hoursKey = "4-" + DateUtil.hour(minuteTime, true);
            double monthWeight = settingMap.get(monthKey) == null ? 1.0 : settingMap.get(monthKey);
            double dayWeight = settingMap.get(dayKey) == null ? 1.0 : settingMap.get(dayKey);
            double weekWeight = settingMap.get(weekKey) == null ? 1.0 : settingMap.get(weekKey);
            double hourWeight = settingMap.get(hoursKey) == null ? 1.0 : settingMap.get(hoursKey);
            double weight = monthWeight * dayWeight * weekWeight * hourWeight;
            weightModel.setValue(weight);
            TjViewData tjViewData = new TjViewData();
            TjDataPk tjDataPk = new TjDataPk();
            tjDataPk.setOrgId(tjHisQuota.getOrgId()).setTjDate(minuteTime);
            tjViewData.setId(tjDataPk);
            minuteWeightMap.put(weightModel, tjViewData);
        });

        // 生成随机历史数据
        long start = System.currentTimeMillis();
        List<TjViewData> tjViewDataList = RandomUtil.buildHisDataFromWeight(tjHisQuota, minuteWeightMap, 0.3);
        log.info("生成{}条随机历史数据,耗时：{} 毫秒", tjViewDataList.size(), DateUtil.spendMs(start));
        log.info("开始随机历史数据插入...");
        start = System.currentTimeMillis();
        int index = 0;
        while (index < tjViewDataList.size()) {
            int tempIndex = index + 5000;
            tjViewDataRepository.saveAll(tjViewDataList.subList(index, tempIndex > tjViewDataList.size() ? tjViewDataList.size() : tempIndex));
            index = tempIndex;
        }
        // 修改状态
        tjHisQuota.setBuilt(true);
        tjHisQuotaRepository.save(tjHisQuota);
        log.info("随机历史数据插入数据库完毕,耗时：{} 毫秒", DateUtil.spendMs(start));
        return true;
    }

    @Override
    public Page<TjViewData> getViewDate(Long orgId, Date beginTime, Date entTime) {
        //tjViewDataRepository.findByTjDateAndOrgIdDay()
        return null;
    }
}
