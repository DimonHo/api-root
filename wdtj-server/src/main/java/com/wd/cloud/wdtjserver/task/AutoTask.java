package com.wd.cloud.wdtjserver.task;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import com.wd.cloud.wdtjserver.entity.TjTaskData;
import com.wd.cloud.wdtjserver.model.WeightModel;
import com.wd.cloud.wdtjserver.repository.TjDateSettingRepository;
import com.wd.cloud.wdtjserver.repository.TjQuotaRepository;
import com.wd.cloud.wdtjserver.repository.TjTaskDataRepository;
import com.wd.cloud.wdtjserver.utils.DateUtil;
import com.wd.cloud.wdtjserver.utils.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
        Map<String, Float> settingMap = new TreeMap<>();
        // 获取所有比率设置，组装map
        tjDateSettingRepository.findAll().forEach(tjDateSetting -> {
            settingMap.put(tjDateSetting.getDateType() + "-" + tjDateSetting.getDateIndex(), tjDateSetting.getWeight());
        });
        // 获取明天所有的分钟数列表
        List<DateTime> minuteList = DateUtil.rangeToList(DateUtil.beginOfDay(DateUtil.tomorrow()), DateUtil.endOfDay(DateUtil.tomorrow()), DateField.MINUTE);
        Map<WeightModel, TjTaskData> minuteWeightMap = new TreeMap<>();
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
            TjTaskData tjTaskData = new TjTaskData();
            tjTaskData.setTjDate(minuteTime);
            minuteWeightMap.put(weightModel, tjTaskData);
        });

        tjQuotaRepository.findByHistoryIsFalse().forEach(tjDaySetting -> {
            List<TjTaskData> taskDataList = RandomUtil.buildDayDataFromWeight(tjDaySetting, minuteWeightMap, 0.3);
            tjTaskDataRepository.saveAll(taskDataList);
        });
    }

    /**
     * 每分钟执行一次
     */
    @Scheduled(cron = "0/1 * * * * ?")
    public void mergeData() {

    }
}
