package com.wd.cloud.uoserver.repository;


import cn.hutool.core.util.StrUtil;
import com.wd.cloud.uoserver.pojo.entity.OrgCdb;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description
 */
public interface OrgCdbRepository extends JpaRepository<OrgCdb, Long>, JpaSpecificationExecutor<OrgCdb> {

    /**
     * 查询机构馆藏
     * @param orgFlag
     * @return
     */
    List<OrgCdb> findByOrgFlag(String orgFlag);

    /**
     * 获取机构馆藏
     * @param orgFlag
     * @param id
     * @return
     */
    Optional<OrgCdb> findByOrgFlagAndId(String orgFlag, Long id);

    /**
     * 删除机构馆藏
     * @param orgFlag
     * @param id
     */
    void deleteByOrgFlagAndId(String orgFlag, Long id);

    class SpecBuilder {
        public static Specification<OrgCdb> query(String orgFlag, Boolean collection,Boolean local,String keyword) {
            return (Specification<OrgCdb>) (root, query, cb) -> {
                List<Predicate> list = new ArrayList<Predicate>();
                if (StrUtil.isNotBlank(orgFlag)) {
                    list.add(cb.equal(root.get("orgFlag"), orgFlag));
                }
                if (collection != null) {
                    list.add(cb.equal(root.get("collection").as(Boolean.class), collection));
                }
                if (local != null){
                    // local 为true表示localUrl不为空，local为false，表示查询localUrl为空的记录
                    list.add(local? cb.isNotNull(root.get("localUrl")): cb.isNull(root.get("localUrl")));
                }
                if (StrUtil.isNotBlank(keyword)){
                    String likeKey = "%"+keyword+"%";
                    // 馆藏名称或馆藏url模糊查询
                    list.add(cb.or(cb.like(root.get("name"),likeKey),cb.like(root.get("url"),likeKey)));
                }
                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            };
        }
    }
}
