package com.wd.cloud.wdtjserver.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.wdtjserver.entity.*;
import com.wd.cloud.wdtjserver.model.*;
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

import java.sql.Time;
import java.util.*;
import java.util.stream.Collectors;

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
        return tjOrgRepository.findAll(JpaQueryUtil.buildQeuryForTjOrg(orgName, history), pageable);
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
        return tjOrgRepository.findAll(JpaQueryUtil.buildFilterForTjOrg(showPv, showSc, showDc, showDdc, showAvgTime, forbade), pageable);
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
        Map<String, Double> settingMap = new TreeMap<>();
        // 获取所有比率设置，组装map
        tjDateSettingRepository.findAll().forEach(tjDateSetting -> {
            settingMap.put(tjDateSetting.getDateType() + "-" + tjDateSetting.getDateIndex(), tjDateSetting.getWeight());
        });
        // 获取分钟列表
        List<DateTime> hourList = DateUtil.rangeToList(tjHisQuota.getBeginTime(), tjHisQuota.getEndTime(), DateField.HOUR);
        Map<WeightModel, HourTotalModel> minuteWeightMap = new TreeMap<>();
        //计算每分钟比率值放入map中
        hourList.forEach(hourDate -> {
            WeightModel weightModel = new WeightModel();
            weightModel.setName(hourDate.toString());
            String monthKey = "1-" + (DateUtil.month(hourDate) + 1);
            String dayKey = "2-" + (DateUtil.dayOfMonth(hourDate));
            // 周日-周六：1 - 7
            String weekKey = "3-" + (DateUtil.dayOfWeek(hourDate));
            String hoursKey = "4-" + DateUtil.hour(hourDate, true);
            double monthWeight = settingMap.get(monthKey) == null ? 1.0 : settingMap.get(monthKey);
            double dayWeight = settingMap.get(dayKey) == null ? 1.0 : settingMap.get(dayKey);
            double weekWeight = settingMap.get(weekKey) == null ? 1.0 : settingMap.get(weekKey);
            double hourWeight = settingMap.get(hoursKey) == null ? 1.0 : settingMap.get(hoursKey);
            double weight = monthWeight * dayWeight * weekWeight * hourWeight;
            weightModel.setValue(weight);
            HourTotalModel hourTotalModel = new HourTotalModel();
            hourTotalModel.setOrgId(tjHisQuota.getOrgId()).setHourDate(hourDate);
            minuteWeightMap.put(weightModel, hourTotalModel);
        });

        // 生成随机历史数据
        long start = System.currentTimeMillis();
        List<HourTotalModel> hourTotalModelList = RandomUtil.buildHisDataFromWeight(tjHisQuota, minuteWeightMap, 0.3);
        hourTotalModelList.forEach(hourTotalModel -> {
            List<AbstractTjDataEntity> tjDataList = RandomUtil.buildMinuteData(hourTotalModel,TjViewData.class);
            List<TjViewData> tjViewDatas = tjDataList.stream().map(a -> (TjViewData)a).collect(Collectors.toList());
            tjViewDataRepository.saveAll(tjViewDatas);
        });
        // 修改状态
        tjHisQuota.setBuilt(true);
        tjHisQuotaRepository.save(tjHisQuota);
        log.info("随机历史数据插入数据库完毕,耗时：{} 毫秒", DateUtil.spendMs(start));
        return true;
    }

    @Override
    public ViewDataModel getViewDate(Long orgId, String beginTime, String endTime, int viewType) {
        List<Map<String, Object>> viewDatas;
        switch (viewType) {
            case 2:
                viewDatas = tjViewDataRepository.findByTjDateFromDay(orgId, beginTime, endTime);
                break;
            case 3:
                viewDatas = tjViewDataRepository.findByTjDateFromMonth(orgId, beginTime, endTime);
                break;
            case 4:
                viewDatas = tjViewDataRepository.findByTjDateFromYear(orgId, beginTime, endTime);
                break;
            default:
                viewDatas = tjViewDataRepository.findByTjDateFromHours(orgId, beginTime, endTime);
                break;
        }
        ViewDataModel viewDataModel = new ViewDataModel();
        viewDataModel.setOrgId(orgId);
        long sumTime = 0;
        long sumUc = 0;
        for (Map<String, Object> viewData : viewDatas) {
            viewDataModel.getTjDate().add((String) viewData.get("tjDate"));
            viewDataModel.getPvCount().add((Integer) viewData.get("pvCount"));
            viewDataModel.getScCount().add((Integer) viewData.get("scCount"));
            viewDataModel.getDcCount().add((Integer) viewData.get("dcCount"));
            viewDataModel.getDcCount().add((Integer) viewData.get("ddcCount"));
            viewDataModel.getUvCount().add((Integer) viewData.get("uvCount"));
            viewDataModel.getUcCount().add((Integer) viewData.get("ucCount"));
            sumTime += (Long) viewData.get("sumTime");
            sumUc += (Long) viewData.get("ucCount");
            viewDataModel.getAvgTime().add(new Time(sumTime / sumUc));
        }

        return viewDataModel;
    }
}
