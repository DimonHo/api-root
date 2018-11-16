package com.wd.cloud.wdtjserver.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.apifeign.OrgServerApi;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.wdtjserver.entity.TjQuota;
import com.wd.cloud.wdtjserver.entity.TjHisQuota;
import com.wd.cloud.wdtjserver.entity.TjOrg;
import com.wd.cloud.wdtjserver.entity.TjViewData;
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

import java.util.*;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
@Service("tjService")
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
    @Autowired
    OrgServerApi orgServerApi;

    @Override
    public TjOrg save(TjOrg tjOrg) {
        ResponseModel responseModel = orgServerApi.getOrg(tjOrg.getOrgId());
        String orgName = JSONUtil.parseObj(responseModel.getBody(), true).getStr("name");
        if (!responseModel.isError()) {
            //根据学校ID查询是否有该学校
            TjOrg oldTjOrg = tjOrgRepository.findByOrgIdAndHistoryIsFalse(tjOrg.getOrgId());
            if (oldTjOrg != null) {
                //修改History为true
                oldTjOrg.setHistory(true);
                tjOrg.setPid(oldTjOrg.getId());
                tjOrgRepository.save(oldTjOrg);
            }
            tjOrg.setOrgName(orgName);
            return tjOrgRepository.save(tjOrg);
        }
        return null;

    }

    @Override
    public Page<TjOrg> likeOrgName(String orgName, boolean history, Pageable pageable) {
        //如果查询条件为空，那么返回全部符合history状态的记录
        if (StrUtil.isBlank(orgName)) {
            return tjOrgRepository.findByHistory(history, pageable);
        }
        return tjOrgRepository.findByHistoryAndOrgNameLike(history, "%" + orgName + "%", pageable);
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
    public Page<TjOrg> filterByQuota(Boolean showPv, Boolean showSc, Boolean showDc, Boolean showDdc, Boolean showAvgTime, Pageable pageable) {
        return tjOrgRepository.findAll(JpaQueryUtil.buildTjOrgQuery(showPv,showSc,showDc,showDdc,showAvgTime),pageable);
    }

    @Override
    public TjQuota save(TjQuota tjQuota) {
        //根据学校ID查询TjDaySetting是否有数据
        TjQuota oldTjQuota = tjQuotaRepository.findByOrgIdAndHistoryIsFalse(tjQuota.getOrgId());
        if (oldTjQuota != null) {
            oldTjQuota.setHistory(true);
            tjQuota.setPid(oldTjQuota.getId());
            tjQuotaRepository.save(oldTjQuota);
        }
        return tjQuotaRepository.save(tjQuota);
    }

    @Override
    public TjQuota findOrgQuota(Long orgId) {
        return tjQuotaRepository.findByOrgIdAndHistoryIsFalse(orgId);
    }

    @Override
    public Page<TjQuota> findOrgQuota(Long orgId, Boolean history,Pageable pageable) {
        if (history == null){
            return tjQuotaRepository.findByOrgId(orgId ,pageable);
        }else{
            return tjQuotaRepository.findByOrgIdAndHistory(orgId,history,pageable);
        }

    }

    @Override
    public Page<TjQuota> findAll(Boolean history, Pageable pageable) {
        if (history != null){
            return tjQuotaRepository.findByHistory(history,pageable);
        }
        return tjQuotaRepository.findAll(pageable);
    }

    @Override
    public TjHisQuota save(TjHisQuota tjHisQuota) {
        return tjHisQuotaRepository.save(tjHisQuota);
    }

    @Override
    public List<TjQuota> findByHistoryIsFalse() {
        return null;
    }

    @Override
    public TjHisQuota get(Long hisId) {
        return tjHisQuotaRepository.getOne(hisId);
    }

    @Override
    public List<TjHisQuota> findHisSettingByOrg(Long orgId) {
        return tjHisQuotaRepository.findByOrgId(orgId);
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

    @Override
    public List<TjHisQuota> save(Long orgId, List<HisQuotaModel> hisQuotaModels) {
        List<TjHisQuota> tjHisQuotas = new ArrayList<>();
        hisQuotaModels.forEach(hisQuotaModel -> {
            TjHisQuota tjHisQuota = new TjHisQuota();
            tjHisQuota.setAvgTime(hisQuotaModel.getAvgTime())
                    .setBeginTime(hisQuotaModel.getBeginTime())
                    .setEndTime(hisQuotaModel.getEndTime())
                    .setPvCount(hisQuotaModel.getPvCount())
                    .setScCount(hisQuotaModel.getScCount())
                    .setDcCount(hisQuotaModel.getDcCount())
                    .setDdcCount(hisQuotaModel.getDdcCount())
                    .setOrgId(orgId);
            tjHisQuotas.add(tjHisQuota);
        });
        return tjHisQuotaRepository.saveAll(tjHisQuotas);
    }


    /**
     * 生成历史数据
     *
     * @param tjHisQuota
     * @return
     */
    @Override
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
            tjViewData.setTjDate(minuteTime);
            minuteWeightMap.put(weightModel, tjViewData);
        });
        // 生成历史数据
        List<TjViewData> tjViewDataList = RandomUtil.buildHisDataFromWeight(tjHisQuota, minuteWeightMap, 0.3);
        tjViewDataRepository.saveAll(tjViewDataList);
        // 修改状态
        tjHisQuota.setBuilt(true);
        tjHisQuotaRepository.save(tjHisQuota);
        return true;
    }
}
