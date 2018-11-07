package com.wd.cloud.wdtjserver.service;

import com.wd.cloud.wdtjserver.entity.TjDaySetting;
import com.wd.cloud.wdtjserver.entity.TjHisSetting;
import com.wd.cloud.wdtjserver.entity.TjOrg;

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


}
