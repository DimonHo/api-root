package com.wd.cloud.wdtjserver.service.impl;

import com.wd.cloud.wdtjserver.entity.TjDaySetting;
import com.wd.cloud.wdtjserver.entity.TjHisSetting;
import com.wd.cloud.wdtjserver.entity.TjOrg;
import com.wd.cloud.wdtjserver.repository.TjHisSettingRepository;
import com.wd.cloud.wdtjserver.repository.TjOrgRepository;
import com.wd.cloud.wdtjserver.service.TjService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
