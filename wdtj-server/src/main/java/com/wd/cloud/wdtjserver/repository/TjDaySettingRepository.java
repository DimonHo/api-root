package com.wd.cloud.wdtjserver.repository;

import com.wd.cloud.wdtjserver.entity.TjDaySetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
public interface TjDaySettingRepository extends JpaRepository<TjDaySetting, Long> {
    //根据orgId查询
    TjDaySetting findByOrgIdAndHistoryIsFalse(long orgId);

    //查询history为false的数据
    List<TjDaySetting> findByHistoryIsFalse();
}
