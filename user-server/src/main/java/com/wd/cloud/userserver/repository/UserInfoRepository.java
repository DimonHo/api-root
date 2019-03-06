package com.wd.cloud.userserver.repository;

import cn.hutool.core.util.StrUtil;
import com.wd.cloud.userserver.entity.UserInfo;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author He Zhigang
 * @date 2019/1/16
 * @Description:
 */
public interface UserInfoRepository extends JpaRepository<UserInfo, Long>, JpaSpecificationExecutor<UserInfo> {

    Optional<UserInfo> findByUsername(String username);

    UserInfo findByUsernameAndValidated(String username, boolean validated);


    @Query(value = "SELECT SUM(IF((Email!= 'Tourist'),1,0)) AS regrestCount,\n" +
                      "\t SUM(IF((user_type = 1),1,0)) AS userTypeCount,\n" +
                      "\t SUM(IF((permission =1 OR permission = 3 OR permission = 4),1,0)) AS permissionCount,\n" +
                      "\t SUM(IF((is_online = 1),1,0)) AS isOnlineCount,org_name \n" +
                   "FROM user_info WHERE org_id !=0 GROUP BY org_id, org_name ORDER BY regrestCount DESC", nativeQuery = true)
    List<Map<String,Object>> getUserInfoSchool(Map<String, Object> params);


    class SpecificationBuilder {
        public static Specification<UserInfo> buildFindUserInfoList(String orgName,String department, String keyword) {
            return (Specification<UserInfo>) (root, query, cb) -> {
                List<Predicate> list = new ArrayList<Predicate>();
                if (StrUtil.isNotBlank(orgName)) {
                    list.add(cb.equal(root.get("orgName").as(String.class), orgName));
                }
                if (StrUtil.isNotBlank(department)){
                    list.add(cb.equal(root.get("department").as(String.class), department));
                }
                if (StrUtil.isNotBlank(keyword)){
                    list.add(cb.or(
                            cb.like(root.get("username").as(String.class), "%" + keyword.trim() + "%"),
                            cb.like(root.get("email").as(String.class), "%" + keyword + "%"),
                            cb.like(root.get("userType").as(String.class), "%" + keyword + "%")
                            )
                    );
                }
                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            };

        }







        public static Specification<UserInfo> buildBackLiteraList() {
            return (Specification<UserInfo>) (root, query, cb) -> {

                List<Predicate> list = new ArrayList<Predicate>();
                CriteriaQuery<Tuple> cq = cb.createTupleQuery();


                Expression<Integer> regrestCount = cb.sum(root.get("email").as(Integer.class));
                Expression<Integer> userTypeCount = cb.sum(root.get("userType").as(Integer.class));
                Expression<Integer> permissionCount = cb.sum(root.get("permission").as(Integer.class));
                Expression<Integer> isOnlineCount = cb.sum(root.get("isOnline").as(Integer.class));
                Path<String> orgName = root.get("orgName");

                list.add(cb.notEqual(root.get("orgId").as(Integer.class),0));

                cq.select(cb.tuple(orgName, regrestCount,userTypeCount,permissionCount,isOnlineCount)).where((Expression<Boolean>) list).groupBy(orgName).orderBy(cb.desc(regrestCount));

                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            };
        }
    }

}
