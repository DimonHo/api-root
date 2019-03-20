package com.wd.cloud.uoserver.repository;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.StrUtil;
import com.wd.cloud.uoserver.pojo.entity.IpRange;
import com.wd.cloud.uoserver.pojo.entity.Org;
import com.wd.cloud.uoserver.pojo.entity.OrgProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description:
 */
public interface OrgRepository extends JpaRepository<Org, String>, JpaSpecificationExecutor<Org> {

    /**
     * 机构信息
     *
     * @param flag
     * @return
     */
    Optional<Org> findByFlag(String flag);

    /**
     * 机构查询
     *
     * @param name
     * @return
     */
    Org findByName(String name);

    boolean existsByFlagOrName(String flag, String name);

    @Query(value = "select * from org t1 where t1.id in " +
            "(select t2.org_id from org_product t2 where t2.status=?1 AND to_days(t2.end_date) > to_days(now())) order by t1.name",
            countQuery = "select count(*) from org t1 where t1.id in " +
                    "(select t2.org_id from org_product t2 where t2.status=?1 AND to_days(t2.end_date) > to_days(now())) order by t1.name", nativeQuery = true)
    Page<Org> findByStatus(Integer status, Pageable pageable);

    @Query(value = "select * from org t1 where t1.id in (select t2.org_id from org_product t2 where to_days(t2.end_date) < to_days(now())) order by t1.name",
            countQuery = "select count(*) from org t1 where t1.id in (select t2.org_id from org_product t2 where to_days(t2.end_date) < to_days(now())) order by t1.name", nativeQuery = true)
    Page<Org> notFindByStatus(Pageable pageable);


    class SpecificationBuilder {

        /**
         * 机构信息查询
         * @param orgName
         * @param flag
         * @param ip
         * @param prodStatus 产品状态
         * @param isExp 是否过期
         * @param isLike 是否模糊查询
         * @return
         */
        public static Specification<Org> queryOrg(String orgName, String flag, String ip, List<Integer> prodStatus, Boolean isExp, boolean isLike) {
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
                if (CollectionUtil.isNotEmpty(prodStatus) || isExp != null) {
                    Subquery<OrgProduct> prodQuery = query.subquery(OrgProduct.class);
                    Root<OrgProduct> prodRoot = prodQuery.from(OrgProduct.class);
                    List<Predicate> prodWhere = new ArrayList<>();
                    // 产品状态过滤
                    if (CollectionUtil.isNotEmpty(prodStatus)) {
                        Predicate inStatus = cb.in(prodRoot.get("status")).value(prodStatus);
                        prodWhere.add(inStatus);
                    }
                    // 是否过滤过期条件
                    if (isExp != null) {
                        Predicate expDate = isExp ? cb.lessThan(prodRoot.get("expDate"), new Date()) : cb.greaterThanOrEqualTo(prodRoot.get("expDate"), new Date());
                        prodWhere.add(expDate);
                    }
                    prodQuery.select(prodRoot.get("orgFlag")).where(ArrayUtil.toArray(prodWhere,Predicate.class));
                    list.add(cb.in(root.get("flag")).value(prodQuery));
                }
                if (StrUtil.isNotBlank(ip)) {
                    long ipNumber = NetUtil.ipv4ToLong(ip);
                    Subquery<IpRange> subQuery = query.subquery(IpRange.class);
                    Root<IpRange> subRoot = subQuery.from(IpRange.class);
                    subQuery.select(subRoot.get("orgFlag")).where(cb.lessThanOrEqualTo(subRoot.get("beginNumber"), ipNumber), cb.greaterThanOrEqualTo(subRoot.get("endNumber"), ipNumber));
                    list.add(cb.equal(root.get("flag"), subQuery));
                }

                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            };
        }

    }
}
