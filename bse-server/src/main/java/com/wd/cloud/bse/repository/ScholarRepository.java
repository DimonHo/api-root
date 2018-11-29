package com.wd.cloud.bse.repository;

import com.wd.cloud.bse.entity.Scholar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;


/**
 * @author He Zhigang
 * @date 2018/5/7
 * @Description:
 */
public interface ScholarRepository extends JpaRepository<Scholar, Long>, JpaSpecificationExecutor<Scholar> {

    /**
     * 根据scid查询学校
     *
     * @param scid
     * @return
     */
    Scholar findById(int id);

}
