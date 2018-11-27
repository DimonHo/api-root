package com.wd.cloud.wdtjserver.utils;

import cn.hutool.core.util.StrUtil;
import com.wd.cloud.wdtjserver.entity.TjOrg;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/11/16
 * @Description:
 */
public class JpaQueryUtil {


    /**
     * 机构名称或创建用户模糊查询
     *
     * @param query
     * @param history
     * @return
     */
    public static <T> Specification<T> buildLikeQuery(String query, Boolean history) {
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> wheres = new ArrayList<Predicate>();
                if (StrUtil.isNotBlank(query)) {
                    wheres.add(criteriaBuilder.or(criteriaBuilder.like(root.get("createUser"), "%" + query + "%"), criteriaBuilder.like(root.get("orgName"), "%" + query + "%")));
                }
                if (history != null) {
                    wheres.add(criteriaBuilder.equal(root.get("history"), history));
                }
                Predicate[] predicates = new Predicate[wheres.size()];
                return criteriaBuilder.and(wheres.toArray(predicates));
            }
        };
    }

    /**
     * 根据指标状态过滤机构
     *
     * @param showPv
     * @param showSc
     * @param showDc
     * @param showDdc
     * @param showAvgTime
     * @param forbade
     * @return
     */
    public static Specification<TjOrg> buildFilterForTjOrg(Boolean showPv, Boolean showSc, Boolean showDc, Boolean showDdc, Boolean showAvgTime, Boolean forbade) {
        return new Specification<TjOrg>() {
            @Override
            public Predicate toPredicate(Root<TjOrg> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> wheres = new ArrayList<Predicate>();

                if (showPv != null) {
                    wheres.add(criteriaBuilder.equal(root.get("showPv"), showPv));
                }
                if (showSc != null) {
                    wheres.add(criteriaBuilder.equal(root.get("showSc"), showSc));
                }
                if (showDc != null) {
                    wheres.add(criteriaBuilder.equal(root.get("showDc"), showDc));
                }
                if (showDdc != null) {
                    wheres.add(criteriaBuilder.equal(root.get("showDdc"), showDdc));
                }
                if (showAvgTime != null) {
                    wheres.add(criteriaBuilder.equal(root.get("showAvgTime"), showAvgTime));
                }
                if (forbade != null) {
                    wheres.add(criteriaBuilder.equal(root.get("forbade"), forbade));
                }

                Predicate[] predicates = new Predicate[wheres.size()];
                return criteriaBuilder.and(wheres.toArray(predicates));
            }
        };
    }

}
