package com.wd.cloud.uoserver.repository;

import cn.hutool.core.util.StrUtil;
import com.wd.cloud.uoserver.pojo.entity.VUserAudit;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: He Zhigang
 * @Date: 2019/3/18 17:36
 * @Description:
 */
public interface VUserAuditRepository extends JpaRepository<VUserAudit, Long>, JpaSpecificationExecutor<VUserAudit> {



    class SpecBuilder {
        /** 模糊查询*/
        public static Specification<VUserAudit> like(Integer status, String keyword) {
            return (Specification<VUserAudit>) (root, query, cb) -> {
                List<Predicate> list = new ArrayList<Predicate>();
                if (status != null && status != -1) {
                    list.add(cb.equal(root.get("status").as(Integer.class), status));
                }
                if (StrUtil.isNotBlank(keyword)) {
                    String likeKey = "%" + keyword + "%";
                    list.add(cb.or(
                            cb.like(root.get("username"), likeKey),
                            cb.like(root.get("nickname"), likeKey),
                            cb.like(root.get("orgName"), likeKey),
                            cb.like(root.get("email"), likeKey)
                    ));
                }
                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            };
        }
    }

}
