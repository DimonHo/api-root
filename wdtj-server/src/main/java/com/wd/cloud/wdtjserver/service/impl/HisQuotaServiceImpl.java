package com.wd.cloud.wdtjserver.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.WeightRandom;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.wdtjserver.entity.*;
import com.wd.cloud.wdtjserver.feign.OrgServerApi;
import com.wd.cloud.wdtjserver.model.DateIntervalModel;
import com.wd.cloud.wdtjserver.model.HisQuotaModel;
import com.wd.cloud.wdtjserver.model.HourTotalModel;
import com.wd.cloud.wdtjserver.repository.TjHisBuildRepository;
import com.wd.cloud.wdtjserver.repository.TjWeightRepository;
import com.wd.cloud.wdtjserver.repository.TjHisQuotaRepository;
import com.wd.cloud.wdtjserver.repository.TjViewDataRepository;
import com.wd.cloud.wdtjserver.service.HisQuotaService;
import com.wd.cloud.wdtjserver.utils.DateUtil;
import com.wd.cloud.wdtjserver.utils.JpaQueryUtil;
import com.wd.cloud.wdtjserver.utils.ModelUtil;
import com.wd.cloud.wdtjserver.utils.RandomUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
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
    TjHisBuildRepository tjHisBuildRepository;

    @Autowired
    TjWeightRepository tjWeightRepository;

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
    @Async
    @Override
    public void buildExecute(TjHisQuota tjHisQuota) {
        Map<String, Double> settingMap = new HashMap<>();
        // 获取所有比率设置，组装map
        tjWeightRepository.findAll().forEach(tjDateSetting -> {
            settingMap.put(tjDateSetting.getDateType() + "-" + tjDateSetting.getDateIndex(), tjDateSetting.getWeight());
        });
        // 获取分钟列表
        List<DateTime> minuteList = DateUtil.rangeToList(tjHisQuota.getBeginTime(), tjHisQuota.getEndTime(), DateField.MINUTE);
        log.info("待生成[{} - {}]共{}分钟数据", tjHisQuota.getBeginTime(), tjHisQuota.getEndTime(), minuteList.size());
        // 计算每分钟的权重
        List<WeightRandom.WeightObj<DateTime>> minuteWeightList = RandomUtil.getWeightList(settingMap, minuteList);
        // 生成随机历史数据
        long start = System.currentTimeMillis();
        //生成每个小时的指标总量
        List<TjViewData> hourTotalModelList = RandomUtil.buildHisDataFromWeight(tjHisQuota, minuteWeightList);
        int startSize = 0;
        int endSize = 5000;
        while (endSize < hourTotalModelList.size()){
            tjViewDataRepository.saveAll(hourTotalModelList.subList(startSize,endSize));
            startSize += 5000;
            endSize += 5000;
        }
        if (startSize < hourTotalModelList.size()){
            tjViewDataRepository.saveAll(hourTotalModelList.subList(startSize,hourTotalModelList.size()));
        }
        // 修改状态
        tjHisQuota.setBuildState(1);
        tjHisQuotaRepository.save(tjHisQuota);
        log.info("随机历史数据插入数据库完毕,耗时：{} 毫秒", DateUtil.spendMs(start));
    }

    @Override
    public void buildingState(TjHisQuota tjHisQuota,String buildUser){
        tjHisQuota.setBuildState(2);
        TjHisBuild tjHisBuild = new TjHisBuild();
        tjHisBuild.setTjHisQuota(tjHisQuota).setName(buildUser);
        tjHisBuildRepository.save(tjHisBuild);
    }
}
