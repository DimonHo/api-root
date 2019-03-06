package com.wd.cloud.userserver.repository;


import cn.hutool.core.util.StrUtil;
import com.wd.cloud.userserver.entity.AuditIdCard;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface AuditIdCardRepository extends JpaRepository<AuditIdCard, Long>, JpaSpecificationExecutor<AuditIdCard> {
    Optional<AuditIdCard> findByUsername(String userName);


    class SpecificationBuilder {
        public static Specification<AuditIdCard> buildBackendList(Integer status, String keyword) {
            return (Specification<AuditIdCard>) (root, query, cb) -> {
                List<Predicate> list = new ArrayList<Predicate>();
                if (status != null && status != -1) {
                    list.add(cb.equal(root.get("status").as(Integer.class), status));
                }
                if (StrUtil.isNotBlank(keyword)) {
                    list.add(cb.or(
                            cb.like(root.get("username").as(String.class), "%" + keyword.trim() + "%"),
                            cb.like(root.get("email").as(String.class), "%" + keyword + "%"),
                            cb.like(root.get("nickName").as(String.class), "%" + keyword.trim() + "%"),
                            cb.like(root.get("orgName").as(String.class), "%" + keyword + "%")
                            )
                    );
                }
                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            };
        }
    }
}
