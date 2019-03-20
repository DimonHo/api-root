package com.wd.cloud.uoserver.repository;

import cn.hutool.core.util.StrUtil;
import com.wd.cloud.uoserver.pojo.entity.VUser;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: He Zhigang
 * @Date: 2019/3/18 18:17
 * @Description:
 */
public interface VUserRepository extends JpaRepository<VUser,String>, JpaSpecificationExecutor<VUser> {



    class SpecBuilder{
        public static Specification<VUser> like(String orgFlag,String orgName,Long departmentId,String department,Integer userType,String keyword) {
            return (Specification<VUser>) (root, query, cb) -> {
                List<Predicate> list = new ArrayList<Predicate>();
                if (StrUtil.isNotBlank(orgFlag)){
                    list.add(cb.equal(root.get("orgFlag"),orgFlag));
                }
                if (StrUtil.isNotBlank(orgName)){
                    list.add(cb.equal(root.get("orgName"),orgName));
                }
                if (departmentId != null){
                    list.add(cb.equal(root.get("departmentId"),departmentId));
                }
                if (StrUtil.isNotBlank(department)){
                    list.add(cb.equal(root.get("departmentName"),department));
                }
                if (userType != null){
                    list.add(cb.equal(root.get("userType"),userType));
                }
                if (StrUtil.isNotBlank(keyword)){
                    String likeKey = "%"+keyword+"%";
                    list.add(cb.or(
                            cb.like(root.get("username"),likeKey),
                            cb.like(root.get("nickname"),likeKey),
                            cb.like(root.get("realname"),likeKey),
                            cb.like(root.get("email"),likeKey)
                    ));
                }
                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            };
        }
    }
}
