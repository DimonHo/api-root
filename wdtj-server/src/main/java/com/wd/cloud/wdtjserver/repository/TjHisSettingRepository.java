package com.wd.cloud.wdtjserver.repository;

import com.wd.cloud.wdtjserver.entity.TjDaySetting;
import com.wd.cloud.wdtjserver.entity.TjHisSetting;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
public interface TjHisSettingRepository extends JpaRepository<TjHisSetting,Long> {

    TjHisSetting findByOrOrgId(long orgId);
}
