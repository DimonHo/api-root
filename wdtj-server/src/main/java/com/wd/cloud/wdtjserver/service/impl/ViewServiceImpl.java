package com.wd.cloud.wdtjserver.service.impl;

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
import java.util.List;
import java.util.Map;

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
        String format = DateUtil.formatMysqlStr(viewType);
        List<Map<String, Object>> viewDatas = tjViewDataRepository.groupByTjDate(orgId, beginTime, endTime, format);
        ViewDataModel viewDataModel = new ViewDataModel();
        viewDataModel.setOrgId(orgId);
        for (Map<String, Object> viewData : viewDatas) {
            viewDataModel.getTjDate().add((String) viewData.get("tjDate"));
            if (tjOrg.isShowPv()) {
                viewDataModel.getPvCount().add(((BigDecimal) viewData.get("pvCount")).intValue());
            }
            if (tjOrg.isShowSc()) {
                viewDataModel.getScCount().add(((BigDecimal) viewData.get("scCount")).intValue());
            }
            if (tjOrg.isShowDc()) {
                viewDataModel.getDcCount().add(((BigDecimal) viewData.get("dcCount")).intValue());
            }
            if (tjOrg.isShowDdc()) {
                viewDataModel.getDdcCount().add(((BigDecimal) viewData.get("ddcCount")).intValue());
            }
            if (tjOrg.isShowUv()) {
                viewDataModel.getUvCount().add(((BigDecimal) viewData.get("uvCount")).intValue());
            }
            long sumTime = ((BigDecimal) viewData.get("sumTime")).longValue();
            long sumUc = ((BigDecimal) viewData.get("ucCount")).longValue();
            if (tjOrg.isShowUc()) {
                viewDataModel.getUcCount().add((int) sumUc);
            }
            if (tjOrg.isShowAvgTime()) {
                long avgTime = sumUc == 0 ? sumTime : sumTime / sumUc;
                viewDataModel.getAvgTime().add(avgTime);
            }
        }
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
}
