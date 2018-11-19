package com.wd.cloud.wdtjserver.task;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import com.wd.cloud.wdtjserver.entity.AbstractTjDataEntity;
import com.wd.cloud.wdtjserver.entity.TjQuota;
import com.wd.cloud.wdtjserver.entity.TjTaskData;
import com.wd.cloud.wdtjserver.model.HourTotalModel;
import com.wd.cloud.wdtjserver.model.WeightModel;
import com.wd.cloud.wdtjserver.repository.TjDateSettingRepository;
import com.wd.cloud.wdtjserver.repository.TjQuotaRepository;
import com.wd.cloud.wdtjserver.repository.TjTaskDataRepository;
import com.wd.cloud.wdtjserver.utils.DateUtil;
import com.wd.cloud.wdtjserver.utils.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
@Component
public class AutoTask {

    @Autowired
    TjDateSettingRepository tjDateSettingRepository;

    @Autowired
    TjQuotaRepository tjQuotaRepository;

    @Autowired
    TjTaskDataRepository tjTaskDataRepository;

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
        // 获取明天所有的分钟数列表
        List<DateTime> hourList = DateUtil.rangeToList(DateUtil.beginOfDay(DateUtil.tomorrow()), DateUtil.endOfDay(DateUtil.tomorrow()), DateField.HOUR);
        Map<WeightModel, HourTotalModel> hourWeightMap = new TreeMap<>();
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
            hourTotalModel.setHourDate(hourDate);
            hourWeightMap.put(weightModel, hourTotalModel);
        });

        Pageable pageable = PageRequest.of(0, 100);
        Page<TjQuota> tjQuotas = tjQuotaRepository.findByHistoryIsFalse(pageable);
        buildData(hourWeightMap, tjQuotas);
        // 下一页
        while (tjQuotas.hasNext()) {
            tjQuotas = tjQuotaRepository.findByHistoryIsFalse(tjQuotas.nextPageable());
            buildData(hourWeightMap, tjQuotas);
        }
    }

    private void buildData(Map<WeightModel, HourTotalModel> hourWeightMap, Page<TjQuota> tjQuotas) {
        tjQuotas.getContent().forEach(tjQuota -> {
            List<HourTotalModel> hourTotalModelList = RandomUtil.buildDayDataFromWeight(tjQuota, hourWeightMap, 0.3);
            hourTotalModelList.forEach(hourTotalModel -> {
                List<AbstractTjDataEntity> tjDataList = RandomUtil.buildMinuteData(hourTotalModel, TjTaskData.class);
                List<TjTaskData> tjTaskDatas = tjDataList.stream().map(a -> (TjTaskData) a).collect(Collectors.toList());
                tjTaskDataRepository.saveAll(tjTaskDatas);
            });
        });
    }

    /**
     * 每分钟执行一次
     */
    @Scheduled(cron = "0/1 * * * * ?")
    public void mergeData() {

    }
}
