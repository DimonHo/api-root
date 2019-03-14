package com.wd.cloud.uoserver.repository;


import com.wd.cloud.uoserver.entity.OrgCdb;
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


    List<OrgCdb> findByCdbId(Long cdbId);

    class SpecificationBuilder {
        public static Specification<OrgCdb> findByOrgIdAndCollection(Long orgId,Boolean collection) {
            return (Specification<OrgCdb>) (root, query, cb) -> {
                List<Predicate> list = new ArrayList<Predicate>();
                if (orgId != null) {
                    list.add(cb.equal(root.get("orgId").as(Long.class), orgId));
                }
                if (collection != null) {
                    list.add(cb.equal(root.get("collection").as(Boolean.class), collection));
                }
                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            };
        }

        public static Specification<OrgCdb> findByOrgIdAndLocalUrlIsNotNull(Long orgId) {
            return (Specification<OrgCdb>) (root, query, cb) -> {
                List<Predicate> list = new ArrayList<Predicate>();
                Path localUrl = root.get("localUrl");
                if (orgId != null) {
                    list.add(cb.and(
                            cb.equal(root.get("orgId").as(Long.class), orgId),
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
