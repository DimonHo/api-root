package com.wd.cloud.bse.repository;

import com.wd.cloud.bse.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


/**
 * @author He Zhigang
 * @date 2018/5/7
 * @Description:
 */
public interface SchoolRepository extends JpaRepository<School, Long>, JpaSpecificationExecutor<School> {

    /**
     * 根据学校名查询学校
     *
     * @param scid
     * @return
     */
	public School findByName(String name);

}
