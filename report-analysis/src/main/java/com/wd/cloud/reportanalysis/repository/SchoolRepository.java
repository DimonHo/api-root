package com.wd.cloud.reportanalysis.repository;

import com.wd.cloud.reportanalysis.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


/**
 * @author He Zhigang
 * @date 2018/5/7
 * @Description:
 */
public interface SchoolRepository extends JpaRepository<School, Long>, JpaSpecificationExecutor<School> {

    /**
     * 根据scid查询学校
     *
     * @param scid
     * @return
     */
    School findByScid(int scid);


}
