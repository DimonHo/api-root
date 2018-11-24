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
        String format;
        switch (viewType) {
            case 1:
                format = "%Y-%m-%d %H";
                break;
            case 2:
                format = "%Y-%m-%d";
                break;
            case 3:
                format = "%Y-%m";
                break;
            case 4:
                format = "%Y";
                break;
            default:
                format = "%Y-%m-%d %H:%i";
                break;
        }
        List<Map<String, Object>> viewDatas = tjViewDataRepository.groupByTjDate(orgId, beginTime, endTime, format);
        ViewDataModel viewDataModel = new ViewDataModel();
        viewDataModel.setOrgId(orgId);
        for (Map<String, Object> viewData : viewDatas) {
            viewDataModel.getTjDate().add((String) viewData.get("tjDate"));
            viewDataModel.getPvCount().add(((BigDecimal) viewData.get("pvCount")).intValue());
            viewDataModel.getScCount().add(((BigDecimal) viewData.get("scCount")).intValue());
            viewDataModel.getDcCount().add(((BigDecimal) viewData.get("dcCount")).intValue());
            viewDataModel.getDdcCount().add(((BigDecimal) viewData.get("ddcCount")).intValue());
            viewDataModel.getUvCount().add(((BigDecimal) viewData.get("uvCount")).intValue());
            int sumTime = ((BigDecimal) viewData.get("sumTime")).intValue();
            int sumUc = ((BigDecimal) viewData.get("ucCount")).intValue();
            viewDataModel.getUcCount().add(sumUc);
            int avgTime = sumUc == 0 ? sumTime : sumTime / sumUc;
            viewDataModel.getAvgTime().add(avgTime);
        }

        return viewDataModel;
    }
}
