package com.wd.cloud.wdtjserver.service.impl;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.wdtjserver.entity.TjDaySetting;
import com.wd.cloud.wdtjserver.entity.TjHisSetting;
import com.wd.cloud.wdtjserver.entity.TjOrg;
import com.wd.cloud.wdtjserver.entity.TjViewData;
import com.wd.cloud.wdtjserver.repository.TjDaySettingRepository;
import com.wd.cloud.wdtjserver.repository.TjOrgRepository;
import com.wd.cloud.wdtjserver.repository.TjViewDataRepository;
import com.wd.cloud.wdtjserver.repository.TjHisSettingRepository;
import com.wd.cloud.wdtjserver.repository.TjOrgRepository;
import com.wd.cloud.wdtjserver.service.TjService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
@Service("tjService")
public class TjServiceImpl implements TjService {

    @Autowired
    TjOrgRepository tjOrgRepository;

    @Autowired
    TjHisSettingRepository tjHisSettingRepository;

    @Override
    public List<TjOrg> likeOrgName(String orgName) {
        List<TjOrg> list = tjOrgRepository.findByOrgNameLike("%"+orgName+"%");
        return list;
    }

    @Override
    public TjOrg save(TjOrg tjOrg) {
        return null;
    }

    @Override
    public TjDaySetting save(TjDaySetting tjDaySetting) {
        return null;
    }

    @Override
    public TjHisSetting save(TjHisSetting tjHisSetting) {
        TjHisSetting oldtjHisSetting = tjHisSettingRepository.findByOrgIdAndHistoryIsFalse(tjHisSetting.getOrgId());
        if(oldtjHisSetting==null){
            tjHisSettingRepository.save(tjHisSetting);
        }else {
            tjHisSetting.setHistory(true);
            tjHisSettingRepository.save(oldtjHisSetting);
            TjHisSetting newtjHisSetting=new TjHisSetting();
            newtjHisSetting.setPid(tjHisSetting.getOrgId());
            newtjHisSetting=tjHisSettingRepository.findByOrgIdAndHistoryIsTrue(tjHisSetting.getOrgId());
            tjHisSettingRepository.save(newtjHisSetting);
        }
        return null;
    }
}
