package com.wd.cloud.uoserver.repository;

import cn.hutool.core.collection.CollectionUtil;
import com.wd.cloud.commons.util.StrUtil;
import com.wd.cloud.uoserver.pojo.entity.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

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
     * 用户名或郵箱
     *
     * @param keyword
     * @return
     */
    @Query("from User where username = ?1 or email = ?1")
    Optional<User> findUser(String keyword);

    /**
     * 用戶名或邮箱查询
     * @param username
     * @param email
     * @return
     */
    Optional<User> findByUsernameOrEmail(String username,String email);

    /**
     * 用户名查询
     * @param username
     * @return
     */
    Optional<User> findByUsername(String username);

    /**
     * 查询邮箱
     * @param email
     * @return
     */
    Optional<User> findByEmail(String email);

    /**
     * 统计机构院系用户
     * @param orgFlag
     * @return
     */
    @Query(value = "SELECT\n" +
            "\tsum( 1 ) AS userCount,\n" +
            "\tSUM( IF ( ( user.user_type = 1 ), 1, 0 ) ) AS normalCount,\n" +
            "\tsum( IF ( ( user.user_type = 2 ), 1, 0 ) ) AS orgAdminCount,\n" +
            "\tsum( IF ( ( user.is_online = 1 ), 1, 0 ) ) AS onlineCount,\n" +
            "\tsum( IF ( ( user.department_id = department.id AND user.username IN ( SELECT username FROM permission WHERE type = 1 ) ), 1, 0 ) ) outsideCount,\n" +
            "\tdepartment.name \n" +
            "FROM\n" +
            "\tuser,\n" +
            "\tdepartment \n" +
            "WHERE\n" +
            "\tuser.org_flag = ?1\n" +
            "\tAND user.department_id = department.id \n" +
            "GROUP BY\n" +
            "\tuser.department_id \n" +
            "ORDER BY\n" +
            "\tuserCount DESC",nativeQuery = true)
    List<Map<String,Object>> tjOrgDepartUser(String orgFlag);

    /**
     * 统计机构用户
     * @return userCount：机构总用户数，normalCount 普通用户数 orgAdminCount 机构管理员用户数，onlineCount 在线用户数，outsideCount 有校外权限用户数
     */
    @Query(value = "SELECT\n" +
            "\tsum( 1 ) AS userCount,\n" +
            "\tSUM( IF ( ( user.user_type = 1 ), 1, 0 ) ) AS normalCount,\n" +
            "\tsum( IF ( ( user.user_type = 2 ), 1, 0 ) ) AS orgAdminCount,\n" +
            "\tsum( IF ( ( user.is_online = 1 ), 1, 0 ) ) AS onlineCount,\n" +
            "\tsum( IF ( ( user.org_flag = org.flag AND user.username IN ( SELECT username FROM permission WHERE type = 1 ) ), 1, 0 ) ) AS outsideCount,\n" +
            "\tuser.org_flag,\n" +
            "\torg.name\n" +
            "FROM\n" +
            "\tuser, org \n" +
            "WHERE \n" +
            "\tuser.org_flag = org.flag\n" +
            "GROUP BY\n" +
            "\tuser.org_flag\n" +
            "ORDER BY\n" +
            "\tuserCount DESC",nativeQuery = true)
    List<Map<String,Object>> tjOrgUser();

    class SpecBuilder {

        public static Specification<User> query(String orgFlag, Long departmentId,List<Integer> userType, String keyword) {
            return (Specification<User>) (root, query, cb) -> {
                List<Predicate> list = new ArrayList<Predicate>();
                if (StrUtil.isNotBlank(orgFlag)) {
                    list.add(cb.equal(root.get("orgFlag"), orgFlag));
                }
                if (departmentId != null) {
                    list.add(cb.equal(root.get("departmentId").as(String.class), departmentId));
                }
                if (CollectionUtil.isNotEmpty(userType)){
                    list.add(cb.in(root.get("userType")).value(userType));
                }
                if (StrUtil.isNotBlank(keyword)) {
                    String likeKey = "%" + keyword.replaceAll("\\\\", "\\\\\\\\").trim() + "%";
                    list.add(cb.or(
                            cb.like(root.get("username").as(String.class), likeKey),
                            cb.like(root.get("email").as(String.class), likeKey)
                            )
                    );
                }
                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            };

        }
    }
}
