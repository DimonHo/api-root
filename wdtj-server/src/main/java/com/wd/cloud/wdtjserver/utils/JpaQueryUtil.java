package com.wd.cloud.wdtjserver.utils;

import cn.hutool.core.date.DateUtil;
import com.wd.cloud.wdtjserver.entity.TjOrg;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.util.StringUtils;

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

    public static Specification<TjOrg> buildTjOrgQuery(Boolean showPv,Boolean showSc, Boolean showDc,Boolean showDdc,Boolean showAvgTime){
        return new Specification<TjOrg>(){
            @Override
            public Predicate toPredicate(Root<TjOrg> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();

                if (showPv != null) {
                    list.add(criteriaBuilder.equal(root.get("showPv").as(Boolean.class), showPv));
                }
                if (showSc != null) {
                    list.add(criteriaBuilder.equal(root.get("showSc").as(Boolean.class), showSc));
                }
                if (showDc != null) {
                    list.add(criteriaBuilder.equal(root.get("showDc").as(Boolean.class), showDc));
                }
                if (showDdc != null) {
                    list.add(criteriaBuilder.equal(root.get("showDdc").as(Boolean.class), showDdc));
                }
                if (showAvgTime != null) {
                    list.add(criteriaBuilder.equal(root.get("showAvgTime").as(Boolean.class), showAvgTime));
                }

                Predicate[] predicates = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(predicates));
            }
        };
    }
}
