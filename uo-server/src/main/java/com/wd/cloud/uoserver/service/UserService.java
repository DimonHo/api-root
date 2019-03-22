package com.wd.cloud.uoserver.service;

import com.wd.cloud.commons.dto.UserDTO;
import com.wd.cloud.uoserver.pojo.entity.User;
import com.wd.cloud.uoserver.pojo.entity.VUserAudit;
import com.wd.cloud.uoserver.pojo.vo.BackUserVO;
import com.wd.cloud.uoserver.pojo.vo.PerfectUserVO;
import com.wd.cloud.uoserver.pojo.vo.UserVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description:
 */
public interface UserService {

    /**
     * 构建sso已认证的用户信息
     *
     * @param authInfo
     * @return
     */
    UserDTO buildUserInfo(Map<String, Object> authInfo);

    /**
     * 注册新用户
     *
     * @param userVO
     * @return
     */
    User registerUser(UserVO userVO);

    /**
     * 后台手动添加新用户
     * @param backUserVO
     * @return
     */
    User addUser(BackUserVO backUserVO);

    /**
     * 更新user
     * @param backUserVO
     * @return
     */
    User saveUser(BackUserVO backUserVO);


    /**
     * 完善用户信息
     * @param perfectUserVO
     * @return
     */
    User perfectUser(PerfectUserVO perfectUserVO);

    /**
     * 上传头像
     *
     * @param username
     * @param file
     * @return
     */
    String uploadHeadImg(String username, MultipartFile file);

    /**
     * 上传证件照
     * @param username
     * @param file
     * @return
     */
    String uploadIdPhoto(String username, MultipartFile file);

    /**
     * 审核验证证件照
     *
     * @param username 被审核用户名
     * @param validated 审核通过or不通过
     * @param handlerName 审核人
     */
    void auditIdPhoto(String username, Boolean validated,String handlerName);

    /**
     * 查询用户信息
     *
     * @param id username or email
     * @return
     */
    UserDTO getUserDTO(String id);

    /**
     * 用户查询
     * @param orgFlag
     * @param orgName
     * @param departmentId
     * @param department
     * @param userType
     * @param keyword
     * @param pageable
     * @return
     */
    Page<UserDTO> queryUsers(String orgFlag, String orgName, Long departmentId, String department, List<Integer> userType, String keyword, Pageable pageable);

    /**
     * 删除用户
     * @param username
     */
    void deleteUser(String username);

    /**
     * 审核列表
     * @param status
     * @param keyword
     * @param pageable
     * @return
     */
    Page<VUserAudit> validList(Integer status, String keyword, Pageable pageable);

    /**
     * 检查邮箱是否存在
     * @param username
     * @return
     */
    boolean checkUsernameExists(String username);

    /**
     * 检查用户名是否已存在
     * @param email
     * @return
     */
    boolean checkEmailExists(String email);
    /**
     * 构建用户session
     * @param userDTO
     * @param request
     * @param redisTemplate
     * @param redisOperationsSessionRepository
     */
    void buildSession(UserDTO userDTO, HttpServletRequest request, RedisTemplate<String, String> redisTemplate, RedisOperationsSessionRepository redisOperationsSessionRepository);

}
