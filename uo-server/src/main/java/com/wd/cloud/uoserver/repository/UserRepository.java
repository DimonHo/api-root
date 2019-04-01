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
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

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

    void deleteByUsername(String username);

    /**
     * 用户名是否存在
     * @param username
     * @return
     */
    boolean existsByUsername(String username);

    /**
     * 邮箱是否存在
     * @param email
     * @return
     */
    boolean existsByEmail(String email);

    /**
     * 查询邮箱
     * @param email
     * @return
     */
    Optional<User> findByEmail(String email);


    /**
     * 统计院系用户数量
     * @param orgDeptId
     * @return
     */
    Long countByOrgDeptId(Long orgDeptId);

    /**
     * 统计机构院系用户
     * @param orgFlag
     * @return
     */
    @Query(value = "SELECT sum(1) AS userCount,SUM(IF ((USER.user_type=1),1,0)) AS normalCount,sum(IF ((USER.user_type=2),1,0)) AS orgAdminCount,sum(IF ((USER.is_online=1),1,0)) AS onlineCount,sum(IF ((USER.org_dept_id=org_dept.id AND USER.username IN (SELECT username FROM permission WHERE type=1)),1,0)) outsideCount,org_dept.NAME FROM USER,org_dept WHERE USER.org_flag= ?1 AND USER.org_dept_id=org_dept.id GROUP BY USER.org_dept_id ORDER BY userCount DESC",nativeQuery = true)
    List<Map<String,Object>> tjOrgDepartUser(String orgFlag);

    /**
     * 统计机构用户
     * @return userCount：机构总用户数，normalCount 普通用户数 orgAdminCount 机构管理员用户数，onlineCount 在线用户数，outsideCount 有校外权限用户数
     */
    @Query(value = "SELECT sum(1) AS userCount,SUM(IF ((USER.user_type=1),1,0)) AS normalCount,sum(IF ((USER.user_type=2),1,0)) AS orgAdminCount,sum(IF ((USER.is_online=1),1,0)) AS onlineCount,sum(IF ((USER.org_flag=org.flag AND USER.username IN (SELECT username FROM permission WHERE type=1)),1,0)) AS outsideCount,USER.org_flag,org.NAME FROM USER,org WHERE USER.org_flag=org.flag GROUP BY USER.org_flag,org.NAME ORDER BY userCount DESC", nativeQuery = true)
    List<Map<String,Object>> tjOrgUser();

    class SpecBuilder {

        public static Specification<User> query(String orgFlag, Long orgDeptId, List<Integer> userType, Boolean valid, List<Integer> validStatus, String keyword) {
            return (Specification<User>) (root, query, cb) -> {
                List<Predicate> list = new ArrayList<Predicate>();
                if (StrUtil.isNotBlank(orgFlag)) {
                    list.add(cb.equal(root.get("orgFlag"), orgFlag));
                }
                if (orgDeptId != null) {
                    list.add(cb.equal(root.get("orgDeptId").as(String.class), orgDeptId));
                }
                if (CollectionUtil.isNotEmpty(userType)){
                    list.add(cb.in(root.get("userType")).value(userType));
                }
                if (valid != null) {
                    // 如果valid==true? 查询审核记录，否则查询idPhoto未null的
                    list.add(valid ? cb.isNotNull(root.get("idPhoto")) : cb.isNull(root.get("idPhoto")));
                }
                if (CollectionUtil.isNotEmpty(validStatus)){
                    list.add(cb.in(root.get("validStatus")).value(validStatus));
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
