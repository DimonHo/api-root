package com.wd.cloud.wdtjserver.service.impl;

import com.wd.cloud.wdtjserver.entity.TjDaySetting;
import com.wd.cloud.wdtjserver.entity.TjHisSetting;
import com.wd.cloud.wdtjserver.entity.TjOrg;
import com.wd.cloud.wdtjserver.entity.TjViewData;
import com.wd.cloud.wdtjserver.repository.TjDaySettingRepository;
import com.wd.cloud.wdtjserver.repository.TjOrgRepository;
import com.wd.cloud.wdtjserver.repository.TjViewDataRepository;
import com.wd.cloud.wdtjserver.service.TjService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
@Service("tjService")
public class TjServiceImpl implements TjService {

    @Autowired
    TjDaySettingRepository tjDaySettingRepository;

    @Autowired
    TjOrgRepository tjOrgRepository;

    @Autowired
    TjViewDataRepository tjViewDataRepository;

    @Override
    public TjOrg save(TjOrg tjOrg) {
        //根据学校ID查询是否有该学校
        TjOrg oldTjOrg = tjOrgRepository.findByOrgIdAndHistoryIsFalse(tjOrg.getOrgId());
        if (oldTjOrg == null) {
            tjOrg = tjOrgRepository.save(tjOrg);
            System.out.println(tjOrg);
        } else {
            //修改History为true
            oldTjOrg.setHistory(true);
            tjOrgRepository.save(oldTjOrg);
            //根据history的值拿到对应的pid
            tjOrg.setPid(oldTjOrg.getId());
            tjOrg = tjOrgRepository.save(tjOrg);
        }
        return tjOrg;
    }

    @Override
    public TjDaySetting save(TjDaySetting tjDaySetting) {
        //根据学校ID查询TjDaySetting是否有数据
        TjDaySetting oldTjDaySetting = tjDaySettingRepository.findByOrgIdAndHistoryIsFalse(tjDaySetting.getOrgId());
        if (oldTjDaySetting == null) {
            //新增一条数据到TjDaySetting表
            tjDaySetting = tjDaySettingRepository.save(tjDaySetting);
        } else {
            oldTjDaySetting.setHistory(true);
            tjDaySettingRepository.save(oldTjDaySetting);
            //根据history的值拿到对应的pid
            tjDaySetting.setPid(oldTjDaySetting.getId());
            tjDaySetting = tjDaySettingRepository.save(tjDaySetting);
        }
        return tjDaySetting;
    }

    @Override
    public TjHisSetting save(TjHisSetting tjHisSetting) {
        return null;
    }

    @Override
    public List<TjViewData> serach(String sTime, String eTime) {
        List<TjViewData> tjViewDataList = null;
        Specification<TjViewData> querySpecifi = new Specification<TjViewData>() {
            @Override
            public Predicate toPredicate(Root<TjViewData> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<TjViewData> predicates = new ArrayList<>();
                if (StringUtils.isNotBlank(sTime)) {
                    //大于或等于传入时间
                    predicates.add((TjViewData) cb.greaterThanOrEqualTo(root.get("tjDate").as(String.class), sTime));
                }
                if (StringUtils.isNotBlank(eTime)) {
                    //小于或等于传入时间
                    predicates.add((TjViewData) cb.lessThanOrEqualTo(root.get("tjDate").as(String.class), eTime));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));

            }
        };
        tjViewDataList = tjViewDataRepository.findAll(querySpecifi);
        return tjViewDataList;
    }
}
