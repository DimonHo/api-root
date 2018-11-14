package com.wd.cloud.wdtjserver.repository;

import com.wd.cloud.wdtjserver.entity.TjDateSetting;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author He Zhigang
 * @date 2018/11/12
 * @Description:
 */
public interface TjDateSettingRepository extends JpaRepository<TjDateSetting, Long> {

    TjDateSetting findByDateIndexAndDateType(int dateIndex, int dateType);

}
