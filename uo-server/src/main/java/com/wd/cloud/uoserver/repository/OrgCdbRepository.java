package com.wd.cloud.uoserver.repository;


import cn.hutool.core.util.StrUtil;
import com.wd.cloud.uoserver.pojo.entity.OrgCdb;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description
 */
public interface OrgCdbRepository extends JpaRepository<OrgCdb, Long>, JpaSpecificationExecutor<OrgCdb> {

    List<OrgCdb> findByOrgFlag(String orgFlag);

    List<OrgCdb> findByCdbId(Long cdbId);

    class SpecificationBuilder {
        public static Specification<OrgCdb> findByOrgFlagAndCollection(String orgFlag, Boolean collection) {
            return (Specification<OrgCdb>) (root, query, cb) -> {
                List<Predicate> list = new ArrayList<Predicate>();
                if (StrUtil.isNotBlank(orgFlag)) {
                    list.add(cb.equal(root.get("orgFlag").as(Long.class), orgFlag));
                }
                if (collection != null) {
                    list.add(cb.equal(root.get("collection").as(Boolean.class), collection));
                }
                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            };
        }

        public static Specification<OrgCdb> findByOrgFlagAndLocalUrlIsNotNull(String orgFlag) {
            return (Specification<OrgCdb>) (root, query, cb) -> {
                List<Predicate> list = new ArrayList<Predicate>();
                Path localUrl = root.get("localUrl");
                if (StrUtil.isNotBlank(orgFlag)) {
                    list.add(cb.and(
                            cb.equal(root.get("orgFlag").as(Long.class), orgFlag),
                            cb.isNotNull(localUrl)
                            )
                    );
                }

                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            };
        }
    }
}
