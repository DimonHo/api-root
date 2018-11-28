package com.wd.cloud.wdtjserver.service.impl;

import cn.hutool.core.date.DateField;
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
import com.wd.cloud.wdtjserver.model.HourTotalModel;
import com.wd.cloud.wdtjserver.repository.TjWeightRepository;
import com.wd.cloud.wdtjserver.repository.TjQuotaRepository;
import com.wd.cloud.wdtjserver.repository.TjTaskDataRepository;
import com.wd.cloud.wdtjserver.service.QuotaService;
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
        Map<String, Double> settingMap = new TreeMap<>();
        // 获取所有比率设置，组装map
        tjWeightRepository.findAll().forEach(tjDateSetting -> {
            settingMap.put(tjDateSetting.getDateType() + "-" + tjDateSetting.getDateIndex(), tjDateSetting.getWeight());
        });
        // 获取明天所有的小时数列表
        List<DateTime> hourList = DateUtil.rangeToList(DateUtil.beginOfDay(date), DateUtil.endOfDay(date), DateField.HOUR);
        // 获取小时列表
        log.info("待生成[{} - {}]共{}小时数据", DateUtil.beginOfDay(DateUtil.tomorrow()), DateUtil.endOfDay(DateUtil.tomorrow()), hourList.size());
        // 计算每个小时的权重
        List<WeightRandom.WeightObj<DateTime>> hoursWeightList = RandomUtil.getWeightList(settingMap, hourList);
        Pageable pageable = PageRequest.of(0, 100);
        Page<TjQuota> tjQuotas = tjQuotaRepository.findByHistoryIsFalse(pageable);
        buildData(hoursWeightList, tjQuotas);
        // 下一页
        while (tjQuotas.hasNext()) {
            tjQuotas = tjQuotaRepository.findByHistoryIsFalse(tjQuotas.nextPageable());
            buildData(hoursWeightList, tjQuotas);
        }
    }


    public void buildData(List<WeightRandom.WeightObj<DateTime>> hoursWeightList, Page<TjQuota> tjQuotas) {
        tjQuotas.getContent().forEach(tjQuota -> {
            Map<DateTime, HourTotalModel> hourTotalModelHashMap = getDateTimeHourTotalModelMap(hoursWeightList, tjQuota);
            hourTotalModelHashMap.values().forEach(hourTotalModel -> {
                // 生成当前小时每分钟的指标数量
                List<TjTaskData> tjDataList = RandomUtil.buildDataFromWeight(hourTotalModel);
                // 插入数据库
                tjTaskDataRepository.saveAll(tjDataList);
            });
        });
    }

    /**
     * 生成随机波动的数据
     * @param hoursWeightList
     * @param tjQuota
     * @return
     */
    public Map<DateTime, HourTotalModel> getDateTimeHourTotalModelMap(List<WeightRandom.WeightObj<DateTime>> hoursWeightList, TjQuota tjQuota) {
        // key:时间（小时），value：HourTotalModel对象
        Map<DateTime, HourTotalModel> hourTotalModelHashMap = ModelUtil.createResultMap(hoursWeightList, tjQuota.getOrgId(), tjQuota.getOrgName());
        // 计算用户访问总时间 = 平均访问时间 * 访问次数
        long avgTimeTotal = DateUtil.getTimeMillis(tjQuota.getAvgTime()) * tjQuota.getUcCount();
        // 随机生成：size为访问次数且总和等于总时间的随机列表
        List<Long> avgTimeRandomList = RandomUtil.randomLongListFromFinalTotal(avgTimeTotal, tjQuota.getUcCount());

        double r = RandomUtil.randomDouble(-0.3, 0.3);
        int pvTotal = tjQuota.getPvCount() + (int) Math.round(tjQuota.getPvCount() * r);
        int scTotal = tjQuota.getScCount() + (int) Math.round(tjQuota.getPvCount() * r);
        int dcTotal = tjQuota.getDcCount() + (int) Math.round(tjQuota.getPvCount() * r);
        int ddcTotal = tjQuota.getDdcCount() + (int) Math.round(tjQuota.getPvCount() * r);
        int uvTotal = tjQuota.getUvCount() + (int) Math.round(tjQuota.getPvCount() * r);
        int ucTotal = tjQuota.getUcCount() + (int) Math.round(tjQuota.getPvCount() * r);
        // 找出最大的指标
        int maxTotal = Arrays.stream(new int[]{pvTotal, scTotal, dcTotal, ddcTotal, uvTotal, ucTotal}).max().orElse(0);
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
        return hourTotalModelHashMap;
    }

}
