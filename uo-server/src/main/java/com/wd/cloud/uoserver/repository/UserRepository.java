package com.wd.cloud.uoserver.repository;

import com.wd.cloud.commons.util.StrUtil;
import com.wd.cloud.uoserver.pojo.entity.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description:
 */
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {

    /**
     * 用户名查询
     *
     * @param id
     * @return
     */
    @Query("FROM User where username=:id or email =:id")
    Optional<User> findUserById(@Param("id") String id);


    Optional<User> findByEmail(String email);

    @Query(value = "SELECT COUNT(id) AS COUNT,department FROM user WHERE email != 'Tourist' and org_name = ?1 GROUP BY department",nativeQuery = true)
    List<Map<String,Object>> findByCountOrgName(String orgName);




    @Query(value = "SELECT SUM(IF((Email!= 'Tourist'),1,0)) AS regrestCount,\n" +
            "\t SUM(IF((user_type = 1),1,0)) AS userTypeCount,\n" +
            "\t SUM(IF((permission =1 OR permission = 3 OR permission = 4),1,0)) AS permissionCount,\n" +
            "\t SUM(IF((is_online = 1),1,0)) AS isOnlineCount,org_name \n" +
            "FROM user WHERE org_id !=0 GROUP BY org_id, org_name ORDER BY regrestCount DESC", nativeQuery = true)
    List<Map<String,Object>> getUserInfoSchool(Map<String, Object> params);

    class SpecificationBuilder {


        public static Specification<User> buildFindUserInfoList(String orgName, String department, String keyword) {
            return (Specification<User>) (root, query, cb) -> {
                List<Predicate> list = new ArrayList<Predicate>();
                if (StrUtil.isNotBlank(orgName)) {
                    list.add(cb.equal(root.get("orgName").as(String.class), orgName));
                }
                if (StrUtil.isNotBlank(department)) {
                    list.add(cb.equal(root.get("department").as(String.class), department));
                }
                if (StrUtil.isNotBlank(keyword)) {
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
    }
}
