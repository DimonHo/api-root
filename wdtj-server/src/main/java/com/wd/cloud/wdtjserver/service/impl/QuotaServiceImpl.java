package com.wd.cloud.wdtjserver.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.WeightRandom;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.wdtjserver.entity.AbstractTjDataEntity;
import com.wd.cloud.wdtjserver.entity.TjQuota;
import com.wd.cloud.wdtjserver.entity.TjTaskData;
import com.wd.cloud.wdtjserver.feign.OrgServerApi;
import com.wd.cloud.wdtjserver.model.TotalModel;
import com.wd.cloud.wdtjserver.repository.TjQuotaRepository;
import com.wd.cloud.wdtjserver.repository.TjTaskDataRepository;
import com.wd.cloud.wdtjserver.repository.TjWeightRepository;
import com.wd.cloud.wdtjserver.service.QuotaService;
import com.wd.cloud.wdtjserver.service.WeightService;
import com.wd.cloud.wdtjserver.utils.DateUtil;
import com.wd.cloud.wdtjserver.utils.JpaQueryUtil;
import com.wd.cloud.wdtjserver.utils.ModelUtil;
import com.wd.cloud.wdtjserver.utils.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
@Service("quotaService")
@Transactional(rollbackFor = Exception.class)
public class QuotaServiceImpl implements QuotaService {
    private static final Log log = LogFactory.get();
    @Autowired
    TjQuotaRepository tjQuotaRepository;

    @Autowired
    TjTaskDataRepository tjTaskDataRepository;

    @Autowired
    TjWeightRepository tjWeightRepository;

    @Autowired
    WeightService weightService;

    @Autowired
    OrgServerApi orgServerApi;

    @Override
    public TjQuota save(TjQuota tjQuota) {
        ResponseModel responseModel = orgServerApi.getOrg(tjQuota.getOrgId());
        if (!responseModel.isError()) {
            String orgName = JSONUtil.parseObj(responseModel.getBody(), true).getStr("name");
            tjQuota.setOrgName(orgName);
            //根据学校ID查询TjDaySetting是否有数据
            TjQuota oldTjQuota = tjQuotaRepository.findByOrgIdAndHistoryIsFalse(tjQuota.getOrgId());
            if (oldTjQuota != null) {
                oldTjQuota.setHistory(true);
                tjQuota.setPid(oldTjQuota.getId());
                tjQuotaRepository.save(oldTjQuota);
            }
            return tjQuotaRepository.save(tjQuota);
        }
        log.error("机构管理服务调用失败：{}", responseModel.getMessage());
        return null;
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
    public Page<TjQuota> likeQuery(String query, Boolean history, Pageable pageable) {
        Specification<TjQuota> specification = JpaQueryUtil.buildLikeQuery(query, history);
        return tjQuotaRepository.findAll(specification, pageable);
    }


    @Override
    public void runTask(Date date) {
        Map<String,Double> weightMap = weightService.buildWeightMap();
        // 获取小时列表
        log.info("待生成[{} - {}]共1天数据", DateUtil.beginOfDay(date), DateUtil.endOfDay(date));
        // 计算天的权重
        WeightRandom.WeightObj<DateTime> dayWeight = RandomUtil.dayWeight(weightMap, DateUtil.beginOfDay(date));
        Pageable pageable = PageRequest.of(0, 100);
        Page<TjQuota> tjQuotas = tjQuotaRepository.findByHistoryIsFalse(pageable);
        buildData(dayWeight, tjQuotas, date);
        // 下一页
        while (tjQuotas.hasNext()) {
            tjQuotas = tjQuotaRepository.findByHistoryIsFalse(tjQuotas.nextPageable());
            buildData(dayWeight, tjQuotas, date);
        }
    }


    public void buildData(WeightRandom.WeightObj<DateTime> dayWeight, Page<TjQuota> tjQuotas, Date date) {
        tjQuotas.getContent().forEach(tjQuota -> {
            Map<DateTime, TotalModel> dayTotal = randomTotal(tjQuota, dayWeight);
            dayTotal.entrySet().forEach(totalModel -> {
                // 生成当前小时每分钟的指标数量
                List<AbstractTjDataEntity> tjDataList = RandomUtil.buildMinuteTjData(DateUtil.beginOfDay(date), DateUtil.endOfDay(date), totalModel, TjTaskData.class);
                // 插入数据库
                tjTaskDataRepository.saveAll(tjDataList.stream().map(a -> (TjTaskData) a).collect(Collectors.toList()));
            });
        });
    }


    public Map<DateTime, TotalModel> randomTotal(TjQuota tjQuota, WeightRandom.WeightObj<DateTime> dayWeight) {
        Map<DateTime, TotalModel> map = new HashMap<DateTime, TotalModel>();
        TotalModel totalModel = new TotalModel();
        int pvTotal = (int) Math.round(tjQuota.getPvCount() * dayWeight.getWeight());
        int scTotal = (int) Math.round(tjQuota.getScCount() * dayWeight.getWeight());
        int dcTotal = (int) Math.round(tjQuota.getDcCount() * dayWeight.getWeight());
        int ddcTotal = (int) Math.round(tjQuota.getDdcCount() * dayWeight.getWeight());
        int uvTotal = (int) Math.round(tjQuota.getUvCount() * dayWeight.getWeight());
        int vvTotal = (int) Math.round(tjQuota.getVvCount() * dayWeight.getWeight());
        // 计算用户访问总时间 = 平均访问时间 * 访问次数
        long avgTimeTotal = DateUtil.getTimeMillis(tjQuota.getAvgTime()) * vvTotal;

        totalModel.setOrgId(tjQuota.getOrgId())
                .setOrgName(tjQuota.getOrgName())
                .setDate(dayWeight.getObj())
                .setPvTotal(pvTotal)
                .setScTotal(scTotal)
                .setDcTotal(dcTotal)
                .setDdcTotal(ddcTotal)
                .setUvTotal(uvTotal)
                .setVvTotal(vvTotal)
                .setVisitTimeTotal(avgTimeTotal);
        map.put(dayWeight.getObj(), totalModel);

        return map;
    }

}
