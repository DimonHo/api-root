package com.wd.cloud.wdtjserver.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.WeightRandom;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.wdtjserver.entity.AbstractTjDataEntity;
import com.wd.cloud.wdtjserver.entity.TjHisQuota;
import com.wd.cloud.wdtjserver.entity.TjQuota;
import com.wd.cloud.wdtjserver.entity.TjViewData;
import com.wd.cloud.wdtjserver.feign.OrgServerApi;
import com.wd.cloud.wdtjserver.model.DateIntervalModel;
import com.wd.cloud.wdtjserver.model.HisQuotaModel;
import com.wd.cloud.wdtjserver.model.HourTotalModel;
import com.wd.cloud.wdtjserver.repository.TjDateSettingRepository;
import com.wd.cloud.wdtjserver.repository.TjHisQuotaRepository;
import com.wd.cloud.wdtjserver.repository.TjViewDataRepository;
import com.wd.cloud.wdtjserver.service.HisQuotaService;
import com.wd.cloud.wdtjserver.utils.DateUtil;
import com.wd.cloud.wdtjserver.utils.JpaQueryUtil;
import com.wd.cloud.wdtjserver.utils.ModelUtil;
import com.wd.cloud.wdtjserver.utils.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author He Zhigang
 * @date 2018/11/20
 * @Description:
 */
@Service("hisQuotaService")
@Transactional(rollbackFor = Exception.class)
public class HisQuotaServiceImpl implements HisQuotaService {
    private static final Log log = LogFactory.get();

    @Autowired
    TjHisQuotaRepository tjHisQuotaRepository;

    @Autowired
    TjDateSettingRepository tjDateSettingRepository;

    @Autowired
    TjViewDataRepository tjViewDataRepository;

    @Autowired
    OrgServerApi orgServerApi;

    @Override
    public TjHisQuota save(TjHisQuota tjHisQuota) {
        ResponseModel responseModel = orgServerApi.getOrg(tjHisQuota.getOrgId());
        if (!responseModel.isError()) {
            String orgName = JSONUtil.parseObj(responseModel.getBody(), true).getStr("name");
            tjHisQuota.setOrgName(orgName);
            return tjHisQuotaRepository.save(tjHisQuota);
        }
        log.error("机构管理服务调用失败：{}", responseModel.getMessage());
        return null;
    }

    @Override
    public List<TjHisQuota> save(List<TjHisQuota> tjHisQuotas) {
        ResponseModel responseModel = orgServerApi.getOrg(tjHisQuotas.get(0).getOrgId());
        if (!responseModel.isError()) {
            String orgName = JSONUtil.parseObj(responseModel.getBody(), true).getStr("name");
            tjHisQuotas.forEach(tjHisQuota -> tjHisQuota.setOrgName(orgName));
            return tjHisQuotaRepository.saveAll(tjHisQuotas);
        }
        return null;
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
    public Page<TjHisQuota> likeQuery(String query, Boolean history, Pageable pageable) {
        Specification<TjHisQuota> specification = JpaQueryUtil.buildLikeQuery(query, history);
        return tjHisQuotaRepository.findAll(specification, pageable);
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
    public boolean buildTjHisData(TjHisQuota tjHisQuota) {
        Map<String, Double> settingMap = new HashMap<>();
        // 获取所有比率设置，组装map
        tjDateSettingRepository.findAll().forEach(tjDateSetting -> {
            settingMap.put(tjDateSetting.getDateType() + "-" + tjDateSetting.getDateIndex(), tjDateSetting.getWeight());
        });
        // 获取小时列表
        List<DateTime> hourList = DateUtil.rangeToList(tjHisQuota.getBeginTime(), tjHisQuota.getEndTime(), DateField.HOUR);
        log.info("待生成[{} - {}]共{}小时数据", tjHisQuota.getBeginTime(), tjHisQuota.getEndTime(), hourList.size());
        // 计算每个小时的权重
        List<WeightRandom.WeightObj<DateTime>> hoursWeightList = RandomUtil.getWeightList(settingMap, hourList);
        // 生成随机历史数据
        long start = System.currentTimeMillis();
        //生成每个小时的指标总量
        List<HourTotalModel> hourTotalModelList = buildHisDataFromWeight(tjHisQuota, hoursWeightList);
        hourTotalModelList.forEach(hourTotalModel -> {
            // 生成当前小时每分钟的指标数量
            List<AbstractTjDataEntity> tjDataList = RandomUtil.buildMinuteData(hourTotalModel, TjViewData.class);
            // 类型强转
            List<TjViewData> tjViewDatas = tjDataList.stream().map(a -> (TjViewData) a).collect(Collectors.toList());
            // 插入数据库
            tjViewDataRepository.saveAll(tjViewDatas);
        });
        // 修改状态
        tjHisQuota.setBuilt(true);
        tjHisQuotaRepository.save(tjHisQuota);
        log.info("随机历史数据插入数据库完毕,耗时：{} 毫秒", DateUtil.spendMs(start));
        return true;
    }


    /**
     * 生成每个小时的指标总量
     *
     * @param hoursWeightList 小时的权重列表
     * @return 每个小时指标总量
     */
    private List<HourTotalModel> buildHisDataFromWeight(TjHisQuota tjHisQuota, List<WeightRandom.WeightObj<DateTime>> hoursWeightList) {

        int pvTotal = tjHisQuota.getPvCount();
        int scTotal = tjHisQuota.getScCount();
        int dcTotal = tjHisQuota.getDcCount();
        int ddcTotal = tjHisQuota.getDdcCount();
        int uvTotal = tjHisQuota.getUvCount();
        int ucTotal = tjHisQuota.getUcCount();

        // key:时间（小时），value：HourTotalModel对象
        Map<DateTime, HourTotalModel> hourTotalModelHashMap = ModelUtil.createResultMap(hoursWeightList,tjHisQuota.getOrgId());
        // 计算用户访问总时间 = 平均访问时间 * 访问次数
        long avgTimeTotal = DateUtil.getTimeMillis(tjHisQuota.getAvgTime()) * tjHisQuota.getUcCount();
        // 随机生成：size为访问次数且总和等于总时间的随机列表
        List<Long> avgTimeRandomList = RandomUtil.randomLongListFromFinalTotal(avgTimeTotal, tjHisQuota.getUcCount());
        // 找出最大的指标
        int maxTotal = Arrays.stream(new int[]{pvTotal, scTotal, dcTotal, ddcTotal, uvTotal, ucTotal}).max().orElse(0);
        log.info("开始：pv={},sc={},dc={},ddc={},uv={},uc={}", pvTotal, scTotal, dcTotal, ddcTotal, ucTotal, ucTotal);
        for (int i = 0; i < maxTotal; i++) {
            DateTime hourDate = RandomUtil.weightRandom(hoursWeightList).next();
            if (pvTotal > 0) {
                hourTotalModelHashMap.get(hourDate).setPvTotal(hourTotalModelHashMap.get(hourDate).getPvTotal() + 1);
                pvTotal--;
            }
            if (scTotal > 0) {
                hourTotalModelHashMap.get(hourDate).setScTotal(hourTotalModelHashMap.get(hourDate).getScTotal() + 1);
                scTotal--;
            }
            if (uvTotal > 0) {
                hourTotalModelHashMap.get(hourDate).setUvTotal(hourTotalModelHashMap.get(hourDate).getUvTotal() + 1);
                uvTotal--;
            }
            if (ucTotal > 0) {
                hourTotalModelHashMap.get(hourDate).setUcTotal(hourTotalModelHashMap.get(hourDate).getUcTotal() + 1);
                // 从平均时长列表中随机找出一个访问时长
                long randomVisitTime = RandomUtil.randomLongEle(avgTimeRandomList, true).orElse(0L);
                randomVisitTime += hourTotalModelHashMap.get(hourDate).getVisitTimeTotal();
                hourTotalModelHashMap.get(hourDate).setVisitTimeTotal(randomVisitTime);
                ucTotal--;
            }
            if (dcTotal > 0) {
                hourTotalModelHashMap.get(hourDate).setDcTotal(hourTotalModelHashMap.get(hourDate).getDcTotal() + 1);
                dcTotal--;
            }
            if (ddcTotal > 0) {
                hourTotalModelHashMap.get(hourDate).setDdcTotal(hourTotalModelHashMap.get(hourDate).getDdcTotal() + 1);
                ddcTotal--;
            }
        }
        log.info("结束：pv={},sc={},dc={},ddc={},uv={},uc={}", pvTotal, scTotal, dcTotal, ddcTotal, ucTotal, ucTotal);
        return new ArrayList<>(hourTotalModelHashMap.values());
    }

}
