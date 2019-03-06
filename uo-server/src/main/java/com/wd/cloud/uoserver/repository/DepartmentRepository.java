package com.wd.cloud.uoserver.repository;

import com.wd.cloud.uoserver.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description:
 */
public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
