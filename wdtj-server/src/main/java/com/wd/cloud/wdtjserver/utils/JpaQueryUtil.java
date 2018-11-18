package com.wd.cloud.wdtjserver.utils;

import cn.hutool.core.util.StrUtil;
import com.wd.cloud.wdtjserver.entity.TjHisQuota;
import com.wd.cloud.wdtjserver.entity.TjOrg;
import com.wd.cloud.wdtjserver.entity.TjQuota;
import com.wd.cloud.wdtjserver.entity.TjViewData;
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
     *
     * @param orgName
     * @param history
     * @return
     */
    public static Specification<TjOrg> buildQeuryForTjOrg(String orgName, Boolean history) {
        return new Specification<TjOrg>() {
            @Override
            public Predicate toPredicate(Root<TjOrg> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> wheres = new ArrayList<Predicate>();
                if (StrUtil.isNotBlank(orgName)) {
                    wheres.add(criteriaBuilder.like(root.get("orgName"), "%" + orgName + "%"));
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


    public static Specification<TjQuota> buildFilterForTjQuota(Long orgId, Boolean history, String createUser, Date gmtCreate, Date gmtModified) {
        return new Specification<TjQuota>() {
            @Override
            public Predicate toPredicate(Root<TjQuota> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> wheres = new ArrayList<Predicate>();

                if (orgId != null) {
                    wheres.add(criteriaBuilder.equal(root.get("orgId"), orgId));
                }
                if (history != null) {
                    wheres.add(criteriaBuilder.equal(root.get("history"), history));
                }
                if (createUser != null) {
                    wheres.add(criteriaBuilder.equal(root.get("createUser"), createUser));
                }
                if (gmtCreate != null) {
                    wheres.add(criteriaBuilder.greaterThan(root.get("gmtCreate"), gmtCreate));
                }
                if (gmtModified != null) {
                    wheres.add(criteriaBuilder.greaterThan(root.get("gmtModified"), gmtModified));
                }

                Predicate[] predicates = new Predicate[wheres.size()];
                return criteriaBuilder.and(wheres.toArray(predicates));
            }
        };
    }


    public static Specification<TjHisQuota> buildFilterForTjQuota(Long orgId, Boolean history, Boolean locked, Boolean built, String createUser) {
        return new Specification<TjHisQuota>() {
            @Override
            public Predicate toPredicate(Root<TjHisQuota> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> wheres = new ArrayList<Predicate>();

                if (orgId != null) {
                    wheres.add(criteriaBuilder.equal(root.get("orgId"), orgId));
                }
                if (history != null) {
                    wheres.add(criteriaBuilder.equal(root.get("history"), history));
                }
                if (locked != null) {
                    wheres.add(criteriaBuilder.equal(root.get("locked"), locked));
                }
                if (built != null) {
                    wheres.add(criteriaBuilder.equal(root.get("built"), built));
                }
                if (createUser != null) {
                    wheres.add(criteriaBuilder.equal(root.get("createUser"), createUser));
                }

                Predicate[] predicates = new Predicate[wheres.size()];
                return criteriaBuilder.and(wheres.toArray(predicates));
            }
        };
    }


    public static Specification<TjViewData> buildFilterForTjViewData(Long orgId, Date beginTime, Date endTime, int type) {
        return new Specification<TjViewData>() {
            @Override
            public Predicate toPredicate(Root<TjViewData> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> wheres = new ArrayList<Predicate>();

                if (orgId != null) {
                    wheres.add(criteriaBuilder.equal(root.get("orgId"), orgId));
                }
                if (beginTime != null) {
                    wheres.add(criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get("beginTime"), beginTime),
                            criteriaBuilder.lessThanOrEqualTo(root.get("endTime"), endTime != null ? endTime : new Date())));
                }


                Predicate[] predicates = new Predicate[wheres.size()];
                return criteriaBuilder.and(wheres.toArray(predicates));
            }
        };
    }
}
