package com.wd.cloud.wdtjserver.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.commons.util.DateUtil;
import com.wd.cloud.wdtjserver.entity.TjOrg;
import com.wd.cloud.wdtjserver.model.ViewDataModel;
import com.wd.cloud.wdtjserver.repository.TjOrgRepository;
import com.wd.cloud.wdtjserver.repository.TjViewDataRepository;
import com.wd.cloud.wdtjserver.service.ViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
@Service("tjService")
@Transactional(rollbackFor = Exception.class)
public class ViewServiceImpl implements ViewService {

    private static final Log log = LogFactory.get();

    @Autowired
    TjOrgRepository tjOrgRepository;

    @Autowired
    TjViewDataRepository tjViewDataRepository;

    @Override
    public ViewDataModel getViewDate(Long orgId, String beginTime, String endTime, int viewType) {
        TjOrg tjOrg = tjOrgRepository.findByOrgIdAndHistoryIsFalse(orgId);
        if (tjOrg == null) {
            return null;
        }
        String formatSqlTime = DateUtil.formatMysqlStr(viewType);
        String formatTime = DateUtil.formatStr(viewType);

        Date start = beginTime(DateTime.of(beginTime, formatTime), viewType);
        Date end = endTime(DateTime.of(endTime, formatTime), viewType);
        log.info("开始时间 - 结束时间：{} - {}", start, end);

        List<DateTime> dateTimes = DateUtil.rangeToList(start, end, dateField(viewType));

        List<String> tjDates = dateTimes.stream().map(dateTime -> DateUtil.format(dateTime, formatTime)).collect(Collectors.toList());

        List<Map<String, Object>> viewDatas = tjViewDataRepository.groupByTjDate(orgId, DateUtil.formatDateTime(start), DateUtil.formatDateTime(end), formatSqlTime);

        ViewDataModel viewDataModel = new ViewDataModel();
        viewDataModel.setOrgId(orgId).setTjDate(tjDates);

        Map<String, Map<String, Object>> tjDateMap = new HashMap<>();

        for (Map<String, Object> viewData : viewDatas) {
            String tjDateStr = (String) viewData.get("tjDate");
            tjDateMap.put(tjDateStr, viewData);
        }
        AtomicLong sumVisitTime = new AtomicLong();
        tjDates.forEach(tjData -> {
            viewDataModel.getPvCount().add(tjDateMap.get(tjData) == null ? 0 : ((BigDecimal) tjDateMap.get(tjData).get("pvCount")).intValue());

            viewDataModel.getScCount().add(tjDateMap.get(tjData) == null ? 0 : ((BigDecimal) tjDateMap.get(tjData).get("scCount")).intValue());

            viewDataModel.getDcCount().add(tjDateMap.get(tjData) == null ? 0 : ((BigDecimal) tjDateMap.get(tjData).get("dcCount")).intValue());

            viewDataModel.getDdcCount().add(tjDateMap.get(tjData) == null ? 0 : ((BigDecimal) tjDateMap.get(tjData).get("ddcCount")).intValue());

            viewDataModel.getUvCount().add(tjDateMap.get(tjData) == null ? 0 : ((BigDecimal) tjDateMap.get(tjData).get("uvCount")).intValue());

            long sumVv = tjDateMap.get(tjData) == null ? 0 : ((BigDecimal) tjDateMap.get(tjData).get("vvCount")).longValue();
            viewDataModel.getVvCount().add(tjDateMap.get(tjData) == null ? 0 : (int) sumVv);

            long sumTime = tjDateMap.get(tjData) == null ? 0 : ((BigDecimal) tjDateMap.get(tjData).get("sumTime")).longValue();
            sumVisitTime.addAndGet(sumTime);

            long avgTime = sumVv == 0 ? sumTime : sumTime / sumVv;
            viewDataModel.getAvgTime().add(tjDateMap.get(tjData) == null ? 0 : avgTime);
        });
        // 计算总量
        viewDataModel.sumTotal(sumVisitTime.get());
        // 过滤不显示的指标
        if (!tjOrg.isShowAvgTime()) {
            viewDataModel.setAvgTimeTotal(0L).setAvgTime(null);
        }
        if (!tjOrg.isShowPv()) {
            viewDataModel.setPvTotal(0).setPvCount(null);
        }
        if (!tjOrg.isShowSc()) {
            viewDataModel.setScTotal(0).setScCount(null);
        }
        if (!tjOrg.isShowDc()) {
            viewDataModel.setDcTotal(0).setDcCount(null);
        }
        if (!tjOrg.isShowDdc()) {
            viewDataModel.setDdcTotal(0).setDdcCount(null);
        }
        if (!tjOrg.isShowUv()) {
            viewDataModel.setUvTotal(0).setUvCount(null);
        }
        if (!tjOrg.isShowVv()) {
            viewDataModel.setVvTotal(0).setVvCount(null);
        }
        return viewDataModel;
    }


    private DateField dateField(int viewType) {
        switch (viewType) {
            case 1:
                return DateField.HOUR;
            case 2:
                return DateField.DAY_OF_MONTH;
            case 3:
                return DateField.MONTH;
            case 4:
                return DateField.YEAR;
            default:
                return DateField.MINUTE;
        }
    }


    private DateTime endTime(Date date, int viewType) {
        switch (viewType) {
            case 1:
                return DateTime.of(date).setField(DateField.MINUTE, 59).setField(DateField.SECOND, 59);
            case 2:
                return DateUtil.endOfDay(date);
            case 3:
                return DateUtil.endOfMonth(date);
            case 4:
                return DateUtil.endOfYear(date);
            default:
                return DateTime.of(date).setField(DateField.HOUR_OF_DAY, 23).setField(DateField.MINUTE, 59).setField(DateField.SECOND, 59);
        }
    }

    private DateTime beginTime(Date date, int viewType) {
        switch (viewType) {
            case 1:
                return DateTime.of(date).setField(DateField.MINUTE, 00).setField(DateField.SECOND, 00);
            case 2:
                return DateUtil.beginOfDay(date);
            case 3:
                return DateUtil.beginOfMonth(date);
            case 4:
                return DateUtil.beginOfYear(date);
            default:
                return DateTime.of(date).setField(DateField.HOUR_OF_DAY, 00).setField(DateField.MINUTE, 00).setField(DateField.SECOND, 00);
        }
    }
}
