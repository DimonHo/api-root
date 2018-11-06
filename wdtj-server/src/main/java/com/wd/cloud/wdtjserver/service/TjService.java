package com.wd.cloud.wdtjserver.service;

import com.wd.cloud.wdtjserver.entity.TjDaySetting;
import com.wd.cloud.wdtjserver.entity.TjHisSetting;
import com.wd.cloud.wdtjserver.entity.TjOrg;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
public interface TjService {

    TjOrg save(TjOrg tjOrg);

    TjDaySetting save(TjDaySetting tjDaySetting);

    TjHisSetting save(TjHisSetting tjHisSetting);
}
