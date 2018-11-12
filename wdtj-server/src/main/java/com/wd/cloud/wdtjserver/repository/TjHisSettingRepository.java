package com.wd.cloud.wdtjserver.repository;

import com.wd.cloud.wdtjserver.entity.TjDaySetting;
import com.wd.cloud.wdtjserver.entity.TjHisSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
public interface TjHisSettingRepository extends JpaRepository<TjHisSetting,Long> {

    List<TjHisSetting> findByOrgId(long orgId);

}
