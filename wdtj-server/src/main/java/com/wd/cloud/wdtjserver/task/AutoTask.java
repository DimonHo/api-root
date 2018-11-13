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
        tjDateSettingRepository.findAll().forEach(tjDateSetting -> {
            settingMap.put(tjDateSetting.getDateType() + "-" + tjDateSetting.getDateIndex(), tjDateSetting.getProportion());
        });

        List<DateTime> dateList = DateUtil.rangeToList(DateUtil.beginOfDay(DateUtil.tomorrow()), DateUtil.endOfDay(DateUtil.tomorrow()), DateField.MINUTE);
        Map<DateTime, Float> dateTimeDoubleMap = new TreeMap();

        dateList.forEach(dateTime -> {
            String monthKey = "1-" + (DateUtil.month(dateTime) + 1);
            String hoursKey = "4-" + DateUtil.hour(dateTime, true);
            float monthProportion = settingMap.get(monthKey) == null ? 1.0F : settingMap.get(monthKey);
            float hourProportion = settingMap.get(hoursKey) == null ? 1.0F : settingMap.get(hoursKey);
            float proportion = monthProportion * hourProportion;

            dateTimeDoubleMap.put(dateTime, proportion);
        });
        List<TjTaskData> tjTaskDataList = new ArrayList();
        tjDaySettingRepository.findByHistoryIsFalse().forEach(tjDaySetting -> {
            float avgPvCountFromMinute = tjDaySetting.getPvCount() / (24 * 60);
            float avgScCountFromMinute = tjDaySetting.getScCount() / (24 * 60);
            float avgDcCountFromMinute = tjDaySetting.getDcCount() / (24 * 60);
            float avgDdcCountFromMinute = tjDaySetting.getDdcCount() / (24 * 60);
            dateTimeDoubleMap.forEach((date, proportion) -> {
                TjTaskData tjTaskData = new TjTaskData();
                int pvCount = Math.round(avgPvCountFromMinute * proportion + RandomUtil.randomInt(-20, 20));
                int scCount = Math.round(avgScCountFromMinute * proportion + RandomUtil.randomInt(-3, 3));
                int dcCount = Math.round(avgDcCountFromMinute * proportion + RandomUtil.randomInt(-3, 3));
                int ddcCount = Math.round(avgDdcCountFromMinute * proportion + RandomUtil.randomInt(-3, 3));
                tjTaskData.setPvCount(pvCount < 0 ? 0 : pvCount);
                tjTaskData.setScCount(scCount < 0 ? 0 : scCount);
                tjTaskData.setDcCount(dcCount < 0 ? 0 : dcCount);
                tjTaskData.setDdcCount(ddcCount < 0 ? 0 : ddcCount);
                tjTaskData.setAvgTime(new Time(tjDaySetting.getAvgTime().getTime()+RandomUtil.randomLong(-10000,100000)));
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
