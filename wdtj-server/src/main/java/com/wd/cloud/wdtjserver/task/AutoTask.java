package com.wd.cloud.wdtjserver.task;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.WeightRandom;
import cn.hutool.json.JSONObject;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.netflix.discovery.converters.Auto;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.wdtjserver.entity.*;
import com.wd.cloud.wdtjserver.feign.DocDeliveryApi;
import com.wd.cloud.wdtjserver.feign.SearchServerApi;
import com.wd.cloud.wdtjserver.model.HourTotalModel;
import com.wd.cloud.wdtjserver.repository.*;
import com.wd.cloud.wdtjserver.service.TjService;
import com.wd.cloud.wdtjserver.utils.DateUtil;
import com.wd.cloud.wdtjserver.utils.ModelUtil;
import com.wd.cloud.wdtjserver.utils.RandomUtil;
import io.swagger.annotations.ApiImplicitParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
@Component
public class AutoTask {

    private static final Log log = LogFactory.get();

    @Autowired
    TjDateSettingRepository tjDateSettingRepository;

    @Autowired
    TjQuotaRepository tjQuotaRepository;

    @Autowired
    TjTaskDataRepository tjTaskDataRepository;

    @Autowired
    TjViewDataRepository tjViewDataRepository;

    @Autowired
    TjService tjService;

    @Autowired
    TjOrgRepository tjOrgRepository;

    @Autowired
    DocDeliveryApi docDeliveryApi;

    @Autowired
    SearchServerApi searchServerApi;

    @Autowired
    TjSpisDataRepository tjSpisDataRepository;

    /**
     * 每天凌晨0点执行一次
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void auto() {
        Map<String, Double> settingMap = new TreeMap<>();
        // 获取所有比率设置，组装map
        tjDateSettingRepository.findAll().forEach(tjDateSetting -> {
            settingMap.put(tjDateSetting.getDateType() + "-" + tjDateSetting.getDateIndex(), tjDateSetting.getWeight());
        });
        // 获取明天所有的小时数列表
        List<DateTime> hourList = DateUtil.rangeToList(DateUtil.beginOfDay(DateUtil.tomorrow()), DateUtil.endOfDay(DateUtil.tomorrow()), DateField.HOUR);
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

    private void buildData(List<WeightRandom.WeightObj<DateTime>> hoursWeightList, Page<TjQuota> tjQuotas) {
        tjQuotas.getContent().forEach(tjQuota -> {
            Map<DateTime, HourTotalModel> hourTotalModelHashMap = getDateTimeHourTotalModelMap(hoursWeightList, tjQuota);
            hourTotalModelHashMap.values().forEach(hourTotalModel -> {
                // 生成当前小时每分钟的指标数量
                List<AbstractTjDataEntity> tjDataList = RandomUtil.buildMinuteData(hourTotalModel, TjTaskData.class);
                // 类型强转
                List<TjTaskData> tjTaskData = tjDataList.stream().map(a -> (TjTaskData) a).collect(Collectors.toList());
                // 插入数据库
                tjTaskDataRepository.saveAll(tjTaskData);
            });
        });
    }

    private Map<DateTime, HourTotalModel> getDateTimeHourTotalModelMap(List<WeightRandom.WeightObj<DateTime>> hoursWeightList, TjQuota tjQuota) {
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


    /**
     * 每分钟执行一次
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void mergeData() {
        // 获取前一分钟（search-server延迟一分钟）
        Date minuteDate = DateUtil.offsetMinute(new Date(), -1);
        ResponseModel<Map<String, JSONObject>> responseModel = searchServerApi.minuteTj(null, DateUtil.formatDateTime(minuteDate));
        if (responseModel.isError()) {
            log.error("获取搜索量失败:{}", responseModel.getMessage());
            return;
        }
        List<TjTaskData> taskDatas = tjTaskDataRepository.getByTjDate(minuteDate);
        taskDatas.forEach(taskData -> {
            int pvCount = responseModel.getBody().get(taskData.getOrgName()).getInt("pvCount");
            int uvCount = responseModel.getBody().get(taskData.getOrgName()).getInt("uvCount");
            long visitTime = (long) (responseModel.getBody().get(taskData.getOrgName()).getDouble("visitTime") * 1000);

            ResponseModel<Integer> downloads = searchServerApi.downloadsCount(taskData.getOrgName(), DateUtil.formatDateTime(minuteDate));
            if (downloads.isError()) {
                log.error("获取下载量失败:{}", responseModel.getMessage());
            }
            ResponseModel<Integer> delivery = docDeliveryApi.getOrgHelpCount(null, taskData.getOrgName(), taskData.getId().getTjDate(), 0);
            if (delivery.isError()) {
                ResponseModel.fail().setMessage("调用文献传递量失败");
            }
            TjSpisData tjSpisData = new TjSpisData();
            tjSpisData.setPvCount(pvCount)
                    .setUvCount(uvCount)
                    .setUcCount(uvCount < pvCount ? RandomUtil.randomInt(uvCount, pvCount) : 0).setVisitTime(visitTime)
                    .setDcCount(delivery.getBody())
                    .setDdcCount(downloads.getBody())
                    .setId(taskData.getId())
                    .setOrgName(taskData.getOrgName());

            TjViewData tjViewData = new TjViewData();
            tjViewData.setPvCount(taskData.getPvCount() + tjSpisData.getPvCount())
                    .setScCount(taskData.getScCount() + tjSpisData.getScCount())
                    .setVisitTime(taskData.getVisitTime() + tjSpisData.getVisitTime())
                    .setDcCount(taskData.getDcCount() + tjSpisData.getDcCount())
                    .setDdcCount(taskData.getDdcCount() + tjSpisData.getDdcCount())
                    .setUvCount(taskData.getUvCount() + tjSpisData.getUvCount())
                    .setUcCount(taskData.getUcCount() + tjSpisData.getUcCount())
                    .setId(taskData.getId())
                    .setOrgName(taskData.getOrgName());
            tjSpisDataRepository.save(tjSpisData);
            tjViewDataRepository.save(tjViewData);
        });
    }
}
