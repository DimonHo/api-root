package com.wd.cloud.wdtjserver.task;

import cn.hutool.json.JSONObject;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.wdtjserver.entity.TjSpisData;
import com.wd.cloud.wdtjserver.entity.TjTaskData;
import com.wd.cloud.wdtjserver.entity.TjViewData;
import com.wd.cloud.wdtjserver.feign.DocDeliveryApi;
import com.wd.cloud.wdtjserver.feign.SearchServerApi;
import com.wd.cloud.wdtjserver.repository.TjSpisDataRepository;
import com.wd.cloud.wdtjserver.repository.TjTaskDataRepository;
import com.wd.cloud.wdtjserver.repository.TjViewDataRepository;
import com.wd.cloud.wdtjserver.service.QuotaService;
import com.wd.cloud.wdtjserver.utils.DateUtil;
import com.wd.cloud.wdtjserver.utils.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
@Component
public class AutoTask {

    private static final Log log = LogFactory.get();

    @Autowired
    TjSpisDataRepository tjSpisDataRepository;

    @Autowired
    TjTaskDataRepository tjTaskDataRepository;

    @Autowired
    TjViewDataRepository tjViewDataRepository;

    @Autowired
    QuotaService quotaService;

    @Autowired
    DocDeliveryApi docDeliveryApi;

    @Autowired
    SearchServerApi searchServerApi;

    /**
     * 每天凌晨0点执行一次
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void auto() {
        Date date = DateUtil.tomorrow();
        quotaService.runTask(date);
    }


    /**
     * 每分钟执行一次 秒 分 时 天 月 星期 年
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void mergeData() {
        // 获取前一分钟（search-server延迟一分钟）
        Date minuteDate = DateUtil.offsetMinute(new Date(), -1);
        ResponseModel<Map<String, JSONObject>> browserResponse = searchServerApi.minuteTj(null, DateUtil.formatDateTime(minuteDate));
        if (browserResponse.isError()) {
            log.error("获取访问量失败:{}", browserResponse.getMessage());
        }
        List<TjTaskData> taskDatas = tjTaskDataRepository.getByTjDate(DateUtil.formatDateTime(minuteDate));
        log.info("待同步数据量：{}", taskDatas.size());
        List<TjSpisData> spisDataList = new ArrayList<>();
        List<TjViewData> viewDataList = new ArrayList<>();
        taskDatas.forEach(taskData -> {
            ResponseModel<Integer> dcResponse = searchServerApi.downloadsCount(taskData.getOrgName(), DateUtil.formatDateTime(minuteDate));
            ResponseModel<Integer> ddcResponse = docDeliveryApi.getOrgHelpCount(null, taskData.getOrgName(), taskData.getId().getTjDate(), 0);
            int dcCount = 0;
            int ddcCount = 0;
            int pvCount = 0;
            int uvCount = 0;
            long visitTime = 0;
            int ucCount = 0;
            if (!browserResponse.isError()){
                JSONObject orgInfo = browserResponse.getBody().get(taskData.getOrgName());
                pvCount = orgInfo != null ? orgInfo.getInt("pvCount") : 0;
                uvCount = orgInfo != null ? orgInfo.getInt("uvCount") : 0;
                visitTime = (long) (orgInfo != null ? orgInfo.getDouble("visitTime") * 1000 : 0);
                ucCount = uvCount < pvCount ? RandomUtil.randomInt(uvCount, pvCount) : 0;
            }
            if (dcResponse.isError()) {
                log.error("获取下载量失败:{}", dcResponse.getMessage());
            } else {
                dcCount = dcResponse.getBody();
            }
            if (ddcResponse.isError()) {
                log.error("获取文献传递量失败:{}", ddcResponse.getMessage());
            } else {
                ddcCount = ddcResponse.getBody();
            }

            //构建spisData对象
            TjSpisData tjSpisData = new TjSpisData();
            tjSpisData.setPvCount(pvCount)
                    .setUvCount(uvCount)
                    .setUcCount(ucCount)
                    .setVisitTime(visitTime)
                    .setDcCount(dcCount)
                    .setDdcCount(ddcCount)
                    .setId(taskData.getId())
                    .setOrgName(taskData.getOrgName());
            spisDataList.add(tjSpisData);

            // viewData = spisData + taskData
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
            viewDataList.add(tjViewData);
        });
        tjSpisDataRepository.saveAll(spisDataList);
        tjViewDataRepository.saveAll(viewDataList);
    }

}
