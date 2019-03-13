package com.wd.cloud.wdtjserver.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.WeightRandom;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.wdtjserver.entity.AbstractTjDataEntity;
import com.wd.cloud.wdtjserver.entity.TjHisBuild;
import com.wd.cloud.wdtjserver.entity.TjHisQuota;
import com.wd.cloud.wdtjserver.entity.TjViewData;
import com.wd.cloud.wdtjserver.exception.AppException;
import com.wd.cloud.wdtjserver.exception.ExceptionEnum;
import com.wd.cloud.wdtjserver.feign.UoServerApi;
import com.wd.cloud.wdtjserver.model.DateIntervalModel;
import com.wd.cloud.wdtjserver.model.HisQuotaModel;
import com.wd.cloud.wdtjserver.model.TotalModel;
import com.wd.cloud.wdtjserver.repository.TjHisBuildRepository;
import com.wd.cloud.wdtjserver.repository.TjHisQuotaRepository;
import com.wd.cloud.wdtjserver.repository.TjViewDataRepository;
import com.wd.cloud.wdtjserver.service.HisQuotaService;
import com.wd.cloud.wdtjserver.service.WeightService;
import com.wd.cloud.wdtjserver.utils.DateUtil;
import com.wd.cloud.wdtjserver.utils.JpaQueryUtil;
import com.wd.cloud.wdtjserver.utils.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    TjHisBuildRepository tjHisBuildRepository;

    @Autowired
    WeightService weightService;

    @Autowired
    TjViewDataRepository tjViewDataRepository;

    @Autowired
    UoServerApi uoServerApi;

    @Override
    public TjHisQuota save(TjHisQuota tjHisQuota) {
        ResponseModel responseModel = uoServerApi.getOrg(tjHisQuota.getOrgId());
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
        try {
            // 调用org-server服务获取机构信息
            ResponseModel responseModel = uoServerApi.getOrg(tjHisQuotas.get(0).getOrgId());
            if (responseModel.isError()) {
                log.error(responseModel.getMessage());
                throw new AppException(ExceptionEnum.ORG_SERVER);
            }
            String orgName = JSONUtil.parseObj(responseModel.getBody(), true).getStr("name");
            tjHisQuotas.forEach(tjHisQuota -> tjHisQuota.setOrgName(orgName));
            return tjHisQuotaRepository.saveAll(tjHisQuotas);
        } catch (Exception e) {
            throw new AppException(ExceptionEnum.ORG_SERVER);
        }
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
    @Async
    @Override
    public void buildExecute(TjHisQuota tjHisQuota) {

        Map<String, Double> weightMap = weightService.buildWeightMap();
        // 将开始时间和结束时间转换为天
        DateTime beginDay = DateUtil.beginOfDay(tjHisQuota.getBeginTime());
        DateTime endDay = DateUtil.beginOfDay(tjHisQuota.getEndTime());

        List<DateTime> dayList = new ArrayList<>();
        if (beginDay.before(endDay)) {
            //如果不是同一天,得到天数列表
            dayList = DateUtil.rangeToList(beginDay, endDay, DateField.DAY_OF_MONTH);
        } else {
            // 如果是同一天，把这一天加入列表
            dayList.add(beginDay);
        }
        log.info("待生成[{} - {}]共{}天数据", tjHisQuota.getBeginTime(), tjHisQuota.getEndTime(), dayList.size());
        //删除旧的数据
        tjViewDataRepository.deleteByTjDate(tjHisQuota.getOrgId(),DateUtil.formatDateTime(tjHisQuota.getBeginTime()),DateUtil.formatDateTime(tjHisQuota.getEndTime()));
        // 计算每天的权重
        List<WeightRandom.WeightObj<DateTime>> dayWeightList = RandomUtil.dayWeightList(weightMap, dayList);
        // 生成随机历史数据
        long start = System.currentTimeMillis();
        //生成每天的指标总量
        Map<DateTime, TotalModel> dayTotalModelList = RandomUtil.dayTotalFromWeight(tjHisQuota, dayWeightList);
        dayTotalModelList.entrySet().forEach(dayTotalModel -> {
            List<AbstractTjDataEntity> tjViewDataList = RandomUtil.buildMinuteTjData(DateTime.of(tjHisQuota.getBeginTime()), DateTime.of(tjHisQuota.getEndTime()), dayTotalModel, true);
            tjViewDataRepository.saveAll(tjViewDataList.stream().map(a -> (TjViewData) a).collect(Collectors.toList()));
        });

        // 修改状态
        tjHisQuota.setBuildState(1);
        tjHisQuotaRepository.save(tjHisQuota);
        log.info("随机历史数据插入数据库完毕,耗时：{} 毫秒", DateUtil.spendMs(start));
    }

    @Override
    public void buildingState(TjHisQuota tjHisQuota, String buildUser) {
        tjHisQuota.setBuildState(2);
        TjHisBuild tjHisBuild = new TjHisBuild();
        tjHisBuild.setTjHisQuotaId(tjHisQuota.getId()).setName(buildUser);
        tjHisBuildRepository.save(tjHisBuild);
    }
}
