package com.wd.cloud.bse.repository.xk;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.wd.cloud.bse.entity.xk.IndexLog;


/**
 * @author He Zhigang
 * @date 2018/5/7
 * @Description:
 */
public interface IndexLogRepository extends JpaRepository<IndexLog, Long>, JpaSpecificationExecutor<IndexLog> {


}
