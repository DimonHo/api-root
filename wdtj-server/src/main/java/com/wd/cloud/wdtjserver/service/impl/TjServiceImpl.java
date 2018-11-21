package com.wd.cloud.wdtjserver.service.impl;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.wdtjserver.model.ViewDataModel;
import com.wd.cloud.wdtjserver.repository.TjViewDataRepository;
import com.wd.cloud.wdtjserver.service.TjService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
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
        long sumTime = 0;
        long sumUc = 0;
        for (Map<String, Object> viewData : viewDatas) {
            viewDataModel.getTjDate().add((String) viewData.get("tjDate"));
            viewDataModel.getPvCount().add((Integer) viewData.get("pvCount"));
            viewDataModel.getScCount().add((Integer) viewData.get("scCount"));
            viewDataModel.getDcCount().add((Integer) viewData.get("dcCount"));
            viewDataModel.getDcCount().add((Integer) viewData.get("ddcCount"));
            viewDataModel.getUvCount().add((Integer) viewData.get("uvCount"));
            viewDataModel.getUcCount().add((Integer) viewData.get("ucCount"));
            sumTime += (Long) viewData.get("sumTime");
            sumUc += (Long) viewData.get("ucCount");
            viewDataModel.getAvgTime().add(new Time(sumTime / sumUc));
        }

        return viewDataModel;
    }
}
