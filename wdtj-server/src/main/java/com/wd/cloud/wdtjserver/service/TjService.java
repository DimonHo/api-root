package com.wd.cloud.wdtjserver.service;

import com.wd.cloud.wdtjserver.entity.TjDaySetting;
import com.wd.cloud.wdtjserver.entity.TjHisSetting;
import com.wd.cloud.wdtjserver.entity.TjOrg;
import com.wd.cloud.wdtjserver.entity.TjViewData;
import com.wd.cloud.wdtjserver.model.QuotaModel;

import java.util.Date;
import java.util.List;
import java.util.Map;

import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
public interface TjService {
    //查询学校
    List<TjOrg> likeOrgName(String orgName);


    TjOrg save(TjOrg tjOrg);

    TjDaySetting save(TjDaySetting tjDaySetting);

    TjHisSetting save(TjHisSetting tjHisSetting);


    //根据年去查询
    //根据月去查询
    //根据天去查询
    //根据小时去查询

    //根据时间去查询数据(默认是按月数)
    List<TjViewData> serach(Long orgId,String stime, String etime);

}
