package com.wd.cloud.uoserver.repository;

import cn.hutool.core.util.NetUtil;
import com.wd.cloud.commons.util.StrUtil;
import com.wd.cloud.uoserver.entity.IpRange;
import com.wd.cloud.uoserver.entity.Org;
import com.wd.cloud.uoserver.entity.OrgProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.criteria.*;
import javax.persistence.metamodel.Metamodel;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description:
 */
public interface OrgRepository extends JpaRepository<Org, Long>, JpaSpecificationExecutor<Org> {

    @Query(value = "select * from org t1 where t1.id in " +
            "(select t2.org_id from org_product t2 where t2.status=?1 AND to_days(t2.end_date) > to_days(now())) order by t1.name",
                    countQuery = "select count(*) from org t1 where t1.id in " +
                            "(select t2.org_id from org_product t2 where t2.status=?1 AND to_days(t2.end_date) > to_days(now())) order by t1.name",nativeQuery = true)
    Page<Org> findByStatus(Integer status , Pageable pageable);

    @Query(value = "select * from org t1 where t1.id in (select t2.org_id from org_product t2 where to_days(t2.end_date) < to_days(now())) order by t1.name",
            countQuery = "select count(*) from org t1 where t1.id in (select t2.org_id from org_product t2 where to_days(t2.end_date) < to_days(now())) order by t1.name",nativeQuery = true)
    Page<Org> notFindByStatus(Pageable pageable);

    Org findByFlag(String flay);

    Org findByName(String name);


    Optional<Org> findByFlagOrSpisFlagOrEduFlag(String flag, String spisFlag, String eduFlag);

    class SpecificationBuilder {
        public static Specification<Org> findOrg(String orgName, String flag, String spisFlag, String eduFlag, String ip, boolean isLike) {
            return (Specification<Org>) (root, query, cb) -> {
                List<Predicate> list = new ArrayList<Predicate>();
                if (orgName != null) {
                    if (isLike) {
                        //模糊查询
                        list.add(cb.like(root.get("name").as(String.class), "%" + orgName + "%"));
                    } else {
                        //精确查询
                        list.add(cb.equal(root.get("name").as(String.class), orgName));
                    }
                }
                if (flag != null) {
                    list.add(cb.equal(root.get("flag").as(String.class), flag));
                }
                if (spisFlag != null) {
                    list.add(cb.equal(root.get("spisFlag").as(String.class), spisFlag));
                }
                if (eduFlag != null) {
                    list.add(cb.equal(root.get("eduFlag").as(String.class), eduFlag));
                }
                if (ip != null) {
                    long ipNumber = NetUtil.ipv4ToLong(ip);
                    Subquery<IpRange> subQuery = query.subquery(IpRange.class);
                    Root<IpRange> subRoot = subQuery.from(IpRange.class);
                    subQuery.select(subRoot.get("orgId")).where(cb.lessThanOrEqualTo(subRoot.get("beginNumber"), ipNumber), cb.greaterThanOrEqualTo(subRoot.get("endNumber"), ipNumber));
                    list.add(cb.equal(root.get("id"), subQuery));
                }
                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            };
        }

        public static Specification<Org> findByNameAndIp(String orgName,  String ip) {
            return (Specification<Org>) (root, query, cb) -> {
                List<Predicate> list = new ArrayList<Predicate>();
                if (StrUtil.isNotBlank(orgName)) {
                    list.add(cb.like(root.get("name").as(String.class), "%" + orgName + "%"));
                }
                if (StrUtil.isNotBlank(ip)) {
                    long ipNumber = NetUtil.ipv4ToLong(ip);
                    Subquery<IpRange> subQuery = query.subquery(IpRange.class);
                    Root<IpRange> subRoot = subQuery.from(IpRange.class);
                    subQuery.select(subRoot.get("orgId")).where(cb.lessThanOrEqualTo(subRoot.get("beginNumber"), ipNumber), cb.greaterThanOrEqualTo(subRoot.get("endNumber"), ipNumber));
                    list.add(cb.equal(root.get("id"), subQuery));
                }
                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            };
        }

    }
}
