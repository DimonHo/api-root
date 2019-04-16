package com.wd.cloud.uoserver.repository;

import com.wd.cloud.uoserver.pojo.entity.OrgDept;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description:
 */
public interface OrgDeptRepository extends JpaRepository<OrgDept, Long> {

    /**
     * 查询机构院系列表
     *
     * @param orgFlag
     * @return
     */
    List<OrgDept> findByOrgFlag(String orgFlag);

    /**
     * 查找院系
     *
     * @param orgFlag
     * @param name
     * @return
     */
    Optional<OrgDept> findByOrgFlagAndName(String orgFlag, String name);

    /**
     * 查找院系
     *
     * @param orgFlag
     * @param id
     * @return
     */
    Optional<OrgDept> findByOrgFlagAndId(String orgFlag, Long id);

    /**
     * 删除指定院系
     *
     * @param orgFlag
     * @param id
     */
    void deleteByOrgFlagAndId(String orgFlag, Long id);


}
