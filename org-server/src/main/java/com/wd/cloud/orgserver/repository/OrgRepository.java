package com.wd.cloud.orgserver.repository;

import com.wd.cloud.orgserver.entity.Org;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * @author He Zhigang
 * @date 2018/11/5
 * @Description:
 */
public interface OrgRepository extends JpaRepository<Org, Long> {

    @Query("FROM Org order by convert_gbk(name) asc")
    List<Org> getAllOrderByName();

    Optional<Org> findByFlag(String flag);

    Optional<Org> findBySpisFlag(String flag);

    Optional<Org> findByEduFlag(String flag);
}
