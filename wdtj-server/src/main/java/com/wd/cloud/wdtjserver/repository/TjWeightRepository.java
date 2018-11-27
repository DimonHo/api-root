package com.wd.cloud.wdtjserver.repository;

import com.wd.cloud.wdtjserver.entity.TjWeight;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author He Zhigang
 * @date 2018/11/12
 * @Description:
 */
public interface TjWeightRepository extends JpaRepository<TjWeight, Long> {

    TjWeight findByDateIndexAndDateType(int dateIndex, int dateType);

}
