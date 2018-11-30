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
        String formatSqlTime = DateUtil.formatMysqlStr(viewType);
        String formatTime = DateUtil.formatStr(viewType);

        Date start = DateTime.of(beginTime, formatTime);
        Date end = DateTime.of(endTime, formatTime);
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
            if (tjOrg.isShowPv()) {
                viewDataModel.getPvCount().add(tjDateMap.get(tjdate) == null ? 0 : ((BigDecimal) tjDateMap.get(tjdate).get("pvCount")).intValue());
            }
            if (tjOrg.isShowSc()) {
                viewDataModel.getScCount().add(tjDateMap.get(tjdate) == null ? 0 : ((BigDecimal) tjDateMap.get(tjdate).get("scCount")).intValue());
            }
            if (tjOrg.isShowDc()) {
                viewDataModel.getDcCount().add(tjDateMap.get(tjdate) == null ? 0 : ((BigDecimal) tjDateMap.get(tjdate).get("dcCount")).intValue());
            }
            if (tjOrg.isShowDdc()) {
                viewDataModel.getDdcCount().add(tjDateMap.get(tjdate) == null ? 0 : ((BigDecimal) tjDateMap.get(tjdate).get("ddcCount")).intValue());
            }
            if (tjOrg.isShowUv()) {
                viewDataModel.getUvCount().add(tjDateMap.get(tjdate) == null ? 0 : ((BigDecimal) tjDateMap.get(tjdate).get("uvCount")).intValue());
            }
            long sumTime = tjDateMap.get(tjdate) == null ? 0 :((BigDecimal) tjDateMap.get(tjdate).get("sumTime")).longValue();
            long sumUc = tjDateMap.get(tjdate) == null ? 0 : ((BigDecimal) tjDateMap.get(tjdate).get("ucCount")).longValue();
            if (tjOrg.isShowUc()) {
                viewDataModel.getUcCount().add(tjDateMap.get(tjdate) == null ? 0 : (int) sumUc);
            }
            if (tjOrg.isShowAvgTime()) {
                long avgTime = sumUc == 0 ? sumTime : sumTime / sumUc;
                viewDataModel.getAvgTime().add(tjDateMap.get(tjdate) == null ? 0 : avgTime);
            }
        });

        viewDataModel.setPvTotal(viewDataModel.getPvCount().stream().reduce((a, b) -> a + b).orElse(0));
        viewDataModel.setScTotal(viewDataModel.getScCount().stream().reduce((a, b) -> a + b).orElse(0));
        viewDataModel.setDcTotal(viewDataModel.getDcCount().stream().reduce((a, b) -> a + b).orElse(0));
        viewDataModel.setDdcTotal(viewDataModel.getDdcCount().stream().reduce((a, b) -> a + b).orElse(0));
        viewDataModel.setUvTotal(viewDataModel.getUvCount().stream().reduce((a, b) -> a + b).orElse(0));
        viewDataModel.setUcTotal(viewDataModel.getUcCount().stream().reduce((a, b) -> a + b).orElse(0));
        long avgTotal = viewDataModel.getUcTotal() == 0 ? 0 : viewDataModel.getAvgTime().stream().reduce((a, b) -> a + b).orElse(0L) / viewDataModel.getUcTotal();
        viewDataModel.setAvgTimeTotal(avgTotal);
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
