package com.wd.cloud.wdtjserver.task;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.RandomUtil;
import com.wd.cloud.wdtjserver.entity.TjTaskData;
import com.wd.cloud.wdtjserver.repository.TjDateSettingRepository;
import com.wd.cloud.wdtjserver.repository.TjDaySettingRepository;
import com.wd.cloud.wdtjserver.repository.TjTaskDataRepository;
import com.wd.cloud.wdtjserver.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.util.ArrayList;
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
    TjDaySettingRepository tjDaySettingRepository;

    @Autowired
    TjTaskDataRepository tjTaskDataRepository;

    /**
     * 每天凌晨0点执行一次
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void auto() {
        Map<String, Float> settingMap = new TreeMap();
        // 获取所有比率设置，组装map
        tjDateSettingRepository.findAll().forEach(tjDateSetting -> {
            settingMap.put(tjDateSetting.getDateType() + "-" + tjDateSetting.getDateIndex(), tjDateSetting.getWeight());
        });
        // 获取明天所有的分钟数列表
        List<DateTime> minuteList = DateUtil.rangeToList(DateUtil.beginOfDay(DateUtil.tomorrow()), DateUtil.endOfDay(DateUtil.tomorrow()), DateField.MINUTE);
        Map<DateTime, Float> minuteWeightMap = new TreeMap();
        //计算每分钟比率值放入map中
        minuteList.forEach(minuteTime -> {
            String monthKey = "1-" + (DateUtil.month(minuteTime) + 1);
            String hoursKey = "4-" + DateUtil.hour(minuteTime, true);
            float monthWeight = settingMap.get(monthKey) == null ? 1.0F : settingMap.get(monthKey);
            float hourWeight = settingMap.get(hoursKey) == null ? 1.0F : settingMap.get(hoursKey);
            float weight = monthWeight * hourWeight;
            minuteWeightMap.put(minuteTime, weight);
        });
        // 生成task数据
        List<TjTaskData> tjTaskDataList = new ArrayList();
        float dayMinutes = 24 * 60;
        // 查询所有机构的日基数
        tjDaySettingRepository.findByHistoryIsFalse().forEach(tjDaySetting -> {
            // 计算分钟平均值
            float avgPvCountFromMinute = tjDaySetting.getPvCount() / dayMinutes;
            float avgScCountFromMinute = tjDaySetting.getScCount() / dayMinutes;
            float avgDcCountFromMinute = tjDaySetting.getDcCount() / dayMinutes;
            float avgDdcCountFromMinute = tjDaySetting.getDdcCount() / dayMinutes;
            //根据比率计算随机值
            minuteWeightMap.forEach((date, weight) -> {
                TjTaskData tjTaskData = new TjTaskData();
                int pvCount = (int) Math.round(avgPvCountFromMinute * weight * RandomUtil.randomDouble(2));
                int scCount = (int) Math.round(avgScCountFromMinute * weight + RandomUtil.randomDouble(-2, 2));
                int dcCount = (int) Math.round(avgDcCountFromMinute * weight + RandomUtil.randomDouble(-2, 2));
                int ddcCount = (int) Math.round(avgDdcCountFromMinute * weight + RandomUtil.randomDouble(-2, 2));
                long avgTime = Math.round(tjDaySetting.getAvgTime().getTime() * weight + RandomUtil.randomLong(-100000, 100000));
                tjTaskData.setPvCount(pvCount < 0 ? 0 : pvCount);
                tjTaskData.setScCount(scCount < 0 ? 0 : scCount);
                tjTaskData.setDcCount(dcCount < 0 ? 0 : dcCount);
                tjTaskData.setDdcCount(ddcCount < 0 ? 0 : ddcCount);
                tjTaskData.setAvgTime(new Time(avgTime));
                tjTaskData.setOrgId(tjDaySetting.getOrgId());
                tjTaskData.setTjDate(date.toTimestamp());
                tjTaskDataList.add(tjTaskData);
            });
        });
        tjTaskDataRepository.saveAll(tjTaskDataList);
    }

    /**
     * 每分钟执行一次
     */
    @Scheduled(cron = "0/1 * * * * ?")
    public void mergeData() {

    }
}
