package com.wd.cloud.wdtjserver.task;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import com.wd.cloud.apifeign.DocServerApi;
import com.wd.cloud.apifeign.SearchServerApi;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.wdtjserver.entity.*;
import com.wd.cloud.wdtjserver.model.WeightModel;
import com.wd.cloud.wdtjserver.repository.*;
import com.wd.cloud.wdtjserver.service.TjService;
import com.wd.cloud.wdtjserver.utils.DateUtil;
import com.wd.cloud.wdtjserver.utils.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    @Autowired
    TjService tjService;

    @Autowired
    TjOrgRepository tjOrgRepository;

    @Autowired
    DocServerApi docServerApi;

    @Autowired
    SearchServerApi searchServerApi;

    @Autowired
    TjSpisDataRepository tjSpisDataRepository;

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
            minuteWeightMap.put(weightModel, tjTaskData);
        });

        Pageable pageable = PageRequest.of(0, 100);
        Page<TjQuota> tjQuotas = tjQuotaRepository.findByHistoryIsFalse(pageable);
        buildData(minuteWeightMap, tjQuotas);
        // 下一页
        while (tjQuotas.hasNext()) {
            tjQuotas = tjQuotaRepository.findByHistoryIsFalse(tjQuotas.nextPageable());
            buildData(minuteWeightMap, tjQuotas);
        }
    }

    private void buildData(Map<WeightModel, TjTaskData> minuteWeightMap, Page<TjQuota> tjQuotas) {
        tjQuotas.getContent().forEach(tjQuota -> {
            List<TjTaskData> taskDataList = RandomUtil.buildDayDataFromWeight(tjQuota, minuteWeightMap, 0.3);
            tjTaskDataRepository.saveAll(taskDataList);
        });
    }

    /**
     * 每分钟执行一次
     */
    @Scheduled(cron = "0/1 * * * * ?")
    public void mergeData() {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = df.format(new Date());
            Time time = new Time(df.parse(date).getTime());
            String substring = date.substring(0, 16);
            List<TjOrg> tjOrgs = tjOrgRepository.findByHistoryIsFalse();
            for (TjOrg tjOrg : tjOrgs){
                List<Map<String, Object>> list =searchServerApi.indexVisit(tjOrg.getOrgId(), date);
                int pv=Double.valueOf(list.get(0).get("pv").toString()).intValue();
                int uv=Double.valueOf(list.get(0).get("uv").toString()).intValue();

                ResponseModel downloads = searchServerApi.downloadsCount(tjOrg.getOrgName(),substring);
                if (downloads.isError()) {
                    ResponseModel.fail().setMessage("调用下载量接口失败");
                }

                ResponseModel delivery = docServerApi.deliveryCount(tjOrg.getOrgName(),substring);
                if (delivery.isError()) {
                     ResponseModel.fail().setMessage("调用文献传递量失败");
                }
                TjSpisData tjSpisData = new TjSpisData();
                tjSpisData.setPvCount(pv);
                tjSpisData.setScCount(uv);
                tjSpisData.setDcCount((Integer) delivery.getBody());
                tjSpisData.setDdcCount((Integer) downloads.getBody());
                tjSpisDataRepository.save(tjSpisData);
            }

        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
