package com.wd.cloud.uoserver.repository;

import com.wd.cloud.commons.util.StrUtil;
import com.wd.cloud.uoserver.pojo.entity.Cdb;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description:
 */
public interface CdbRepository extends JpaRepository<Cdb, Long>, JpaSpecificationExecutor<Cdb> {


    Cdb findByNameAndUrl(String name,String url);


    class SpecificationBuilder {
        public static Specification<Cdb> findByNameAndUrl(String keyword) {
            return (Specification<Cdb>) (root, query, cb) -> {
                List<Predicate> list = new ArrayList<Predicate>();
                if (StrUtil.isNotBlank(keyword)) {
                    list.add(cb.or(
                            cb.like(root.get("name").as(String.class), "%" + keyword.trim() + "%"),
                            cb.like(root.get("url").as(String.class), "%" + keyword + "%")
                            )
                    );
                }
                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            };
        }
    }
}
