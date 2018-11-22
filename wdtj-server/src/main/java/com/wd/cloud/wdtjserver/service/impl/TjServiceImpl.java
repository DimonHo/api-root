package com.wd.cloud.wdtjserver.service.impl;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.wdtjserver.model.ViewDataModel;
import com.wd.cloud.wdtjserver.repository.TjViewDataRepository;
import com.wd.cloud.wdtjserver.service.TjService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
@Service("tjService")
@Transactional(rollbackFor = Exception.class)
public class TjServiceImpl implements TjService {

    private static final Log log = LogFactory.get();

    @Autowired
    TjViewDataRepository tjViewDataRepository;

    @Override
    public ViewDataModel getViewDate(Long orgId, String beginTime, String endTime, int viewType) {
        List<Map<String, Object>> viewDatas;
        switch (viewType) {
            case 2:
                viewDatas = tjViewDataRepository.findByTjDateFromDay(orgId, beginTime, endTime);
                break;
            case 3:
                viewDatas = tjViewDataRepository.findByTjDateFromMonth(orgId, beginTime, endTime);
                break;
            case 4:
                viewDatas = tjViewDataRepository.findByTjDateFromYear(orgId, beginTime, endTime);
                break;
            default:
                viewDatas = tjViewDataRepository.findByTjDateFromHours(orgId, beginTime, endTime);
                break;
        }
        ViewDataModel viewDataModel = new ViewDataModel();
        viewDataModel.setOrgId(orgId);
        for (Map<String, Object> viewData : viewDatas) {
            viewDataModel.getTjDate().add((String) viewData.get("tjDate"));
            viewDataModel.getPvCount().add((BigDecimal) viewData.get("pvCount"));
            viewDataModel.getScCount().add((BigDecimal) viewData.get("scCount"));
            viewDataModel.getDcCount().add((BigDecimal) viewData.get("dcCount"));
            viewDataModel.getDdcCount().add((BigDecimal) viewData.get("ddcCount"));
            viewDataModel.getUvCount().add((BigDecimal) viewData.get("uvCount"));
            BigDecimal sumTime = (BigDecimal) viewData.get("sumTime");
            BigDecimal sumUc = (BigDecimal) viewData.get("ucCount");
            viewDataModel.getUcCount().add(sumUc);
            viewDataModel.getAvgTime().add(sumTime.divide(sumUc, 0, RoundingMode.HALF_UP));
        }

        return viewDataModel;
    }
}
