package com.wd.cloud.uoserver.repository;

import com.wd.cloud.uoserver.pojo.entity.OrgLinkman;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description:
 */
public interface OrgLinkmanRepository extends JpaRepository<OrgLinkman, Long> {

    /**
     * 查询机构联系人
     *
     * @param orgFlag 机构标示
     * @return 联系人列表
     */
    List<OrgLinkman> findByOrgFlag(String orgFlag);


    /**
     * 获取联系人
     *
     * @param orgFlag
     * @param id
     * @return
     */
    Optional<OrgLinkman> findByOrgFlagAndId(String orgFlag, Long id);

    /**
     * 删除联系人
     *
     * @param orgFlag
     * @param id
     */
    void deleteByOrgFlagAndId(String orgFlag, Long id);
}
