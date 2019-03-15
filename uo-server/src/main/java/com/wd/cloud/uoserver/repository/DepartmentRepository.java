package com.wd.cloud.uoserver.repository;

import com.wd.cloud.uoserver.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description:
 */
public interface DepartmentRepository extends JpaRepository<Department, Long> {




    /**
     *
     */

    List<Department> findByOrgId(Long orgId);




}
