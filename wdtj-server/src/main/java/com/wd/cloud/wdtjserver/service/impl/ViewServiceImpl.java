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

        Date start = DateTime.of(beginTime, formatTime);
        Date end = DateTime.of(endTime, formatTime);
        log.info("开始时间 - 结束时间：{} - {}", start, end);

        List<DateTime> dateTimes = DateUtil.rangeToList(start, end, dateField(viewType));

        List<String> tjDates = dateTimes.stream().map(dateTime -> DateUtil.format(dateTime, formatTime)).collect(Collectors.toList());

        List<Map<String, Object>> viewDatas = tjViewDataRepository.groupByTjDate(orgId, beginTime, endTime, formatSqlTime);

        ViewDataModel viewDataModel = new ViewDataModel();
        viewDataModel.setOrgId(orgId).setTjDate(tjDates);

        Map<String, Map<String, Object>> tjDateMap = new HashMap<>();

        for (Map<String, Object> viewData : viewDatas) {
            String tjDateStr = (String) viewData.get("tjDate");
            tjDateMap.put(tjDateStr, viewData);
        }
        tjDates.forEach(tjdate -> {
            viewDataModel.getPvCount().add(tjDateMap.get(tjdate) == null ? 0 : ((BigDecimal) tjDateMap.get(tjdate).get("pvCount")).intValue());

            viewDataModel.getScCount().add(tjDateMap.get(tjdate) == null ? 0 : ((BigDecimal) tjDateMap.get(tjdate).get("scCount")).intValue());

            viewDataModel.getDcCount().add(tjDateMap.get(tjdate) == null ? 0 : ((BigDecimal) tjDateMap.get(tjdate).get("dcCount")).intValue());

            viewDataModel.getDdcCount().add(tjDateMap.get(tjdate) == null ? 0 : ((BigDecimal) tjDateMap.get(tjdate).get("ddcCount")).intValue());

            viewDataModel.getUvCount().add(tjDateMap.get(tjdate) == null ? 0 : ((BigDecimal) tjDateMap.get(tjdate).get("uvCount")).intValue());

            long sumTime = tjDateMap.get(tjdate) == null ? 0 : ((BigDecimal) tjDateMap.get(tjdate).get("sumTime")).longValue();
            long sumUc = tjDateMap.get(tjdate) == null ? 0 : ((BigDecimal) tjDateMap.get(tjdate).get("ucCount")).longValue();
            viewDataModel.getUcCount().add(tjDateMap.get(tjdate) == null ? 0 : (int) sumUc);

            long avgTime = sumUc == 0 ? sumTime : sumTime / sumUc;
            viewDataModel.getAvgTime().add(tjDateMap.get(tjdate) == null ? 0 : avgTime);
        });
        // 计算总量
        viewDataModel.sumTotal();
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
        if (!tjOrg.isShowUc()) {
            viewDataModel.setUcTotal(0).setUcCount(null);
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
}
