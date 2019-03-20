package com.wd.cloud.uoserver.repository;

import com.wd.cloud.uoserver.pojo.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description:
 */
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    /**
     * 查询机构所属院系
     */
    List<Department> findByOrgFlag(String orgFlag);

    Optional<Department> findByOrgFlagAndId(String orgFlag,Long id);

    Optional<Department> findByOrgFlagAndName(String orgFlag, String name);




}
