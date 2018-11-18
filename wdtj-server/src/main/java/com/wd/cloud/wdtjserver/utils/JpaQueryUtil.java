package com.wd.cloud.wdtjserver.utils;

import com.wd.cloud.wdtjserver.entity.TjHisQuota;
import com.wd.cloud.wdtjserver.entity.TjOrg;
import com.wd.cloud.wdtjserver.entity.TjQuota;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/11/16
 * @Description:
 */
public class JpaQueryUtil {

    /**
     * 机构名称模糊查询
     * @param orgName
     * @param history
     * @return
     */
    public static Specification<TjOrg> buildQeuryForTjOrg(String orgName, Boolean history){
        return new Specification<TjOrg>() {
            @Override
            public Predicate toPredicate(Root<TjOrg> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> wheres = new ArrayList<Predicate>();
                if (orgName != null) {
                    wheres.add(criteriaBuilder.like(root.get("orgName").as(String.class), orgName));
                }
                if (history != null) {
                    wheres.add(criteriaBuilder.equal(root.get("history").as(Boolean.class), history));
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
    public static Specification<TjOrg> buildFilterForTjOrg(Boolean showPv, Boolean showSc, Boolean showDc, Boolean showDdc, Boolean showAvgTime,Boolean forbade) {
        return new Specification<TjOrg>() {
            @Override
            public Predicate toPredicate(Root<TjOrg> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> wheres = new ArrayList<Predicate>();

                if (showPv != null) {
                    wheres.add(criteriaBuilder.equal(root.get("showPv").as(Boolean.class), showPv));
                }
                if (showSc != null) {
                    wheres.add(criteriaBuilder.equal(root.get("showSc").as(Boolean.class), showSc));
                }
                if (showDc != null) {
                    wheres.add(criteriaBuilder.equal(root.get("showDc").as(Boolean.class), showDc));
                }
                if (showDdc != null) {
                    wheres.add(criteriaBuilder.equal(root.get("showDdc").as(Boolean.class), showDdc));
                }
                if (showAvgTime != null) {
                    wheres.add(criteriaBuilder.equal(root.get("showAvgTime").as(Boolean.class), showAvgTime));
                }
                if (forbade != null) {
                    wheres.add(criteriaBuilder.equal(root.get("forbade").as(Boolean.class), forbade));
                }

                Predicate[] predicates = new Predicate[wheres.size()];
                return criteriaBuilder.and(wheres.toArray(predicates));
            }
        };
    }


    public static Specification<TjQuota> buildFilterForTjQuota(Long orgId, Boolean history, String createUser, Date gmtCreate, Date gmtModified) {
        return new Specification<TjQuota>() {
            @Override
            public Predicate toPredicate(Root<TjQuota> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> wheres = new ArrayList<Predicate>();

                if (orgId != null) {
                    wheres.add(criteriaBuilder.equal(root.get("orgId").as(Long.class), orgId));
                }
                if (history != null) {
                    wheres.add(criteriaBuilder.equal(root.get("history").as(Boolean.class), history));
                }
                if (createUser != null){
                    wheres.add(criteriaBuilder.equal(root.get("createUser").as(String.class), createUser));
                }
                if (gmtCreate != null){
                    wheres.add(criteriaBuilder.greaterThan(root.get("gmtCreate").as(Date.class),gmtCreate));
                }
                if (gmtModified != null){
                    wheres.add(criteriaBuilder.greaterThan(root.get("gmtModified").as(Date.class),gmtModified));
                }

                Predicate[] predicates = new Predicate[wheres.size()];
                return criteriaBuilder.and(wheres.toArray(predicates));
            }
        };
    }


    public static Specification<TjHisQuota> buildFilterForTjQuota(Long orgId, Boolean history,Boolean locked,Boolean built, String createUser) {
        return new Specification<TjHisQuota>() {
            @Override
            public Predicate toPredicate(Root<TjHisQuota> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> wheres = new ArrayList<Predicate>();

                if (orgId != null) {
                    wheres.add(criteriaBuilder.equal(root.get("orgId").as(Long.class), orgId));
                }
                if (history != null) {
                    wheres.add(criteriaBuilder.equal(root.get("history").as(Boolean.class), history));
                }
                if (locked != null) {
                    wheres.add(criteriaBuilder.equal(root.get("locked").as(Boolean.class), locked));
                }
                if (built != null) {
                    wheres.add(criteriaBuilder.equal(root.get("built").as(Boolean.class), built));
                }
                if (createUser != null){
                    wheres.add(criteriaBuilder.equal(root.get("createUser").as(String.class), createUser));
                }

                Predicate[] predicates = new Predicate[wheres.size()];
                return criteriaBuilder.and(wheres.toArray(predicates));
            }
        };
    }
}
