package com.wd.cloud.docdelivery.repository;

import com.wd.cloud.docdelivery.entity.Literature;
import com.wd.cloud.docdelivery.entity.VLiterature;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public interface VLiteratureRepository extends JpaRepository<VLiterature, Long>, JpaSpecificationExecutor<VLiterature> {

    class SpecificationBuilder {
        public static Specification<VLiterature> buildBackLiteraList(Boolean reusing,String keyword){
            return (Specification<VLiterature>) (root, query, cb) -> {
                List<Predicate> list = new ArrayList<Predicate>();
                if (reusing != null) {
                    list.add(cb.equal(root.get("reusing").as(boolean.class), reusing));
                }
                if (!StringUtils.isEmpty(keyword)) {
                    list.add(cb.like(root.get("docTitle").as(String.class), "%" + keyword + "%"));
                }
                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            };
        }
    }
}
