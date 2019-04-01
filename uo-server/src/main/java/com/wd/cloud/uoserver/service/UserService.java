package com.wd.cloud.uoserver.service;

import com.wd.cloud.uoserver.pojo.dto.UserDTO;
import com.wd.cloud.uoserver.pojo.entity.User;
import com.wd.cloud.uoserver.pojo.vo.BackUserVO;
import com.wd.cloud.uoserver.pojo.vo.PerfectUserVO;
import com.wd.cloud.uoserver.pojo.vo.PermissionVO;
import com.wd.cloud.uoserver.pojo.vo.UserVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description:
 */
public interface UserService {

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
     * @param remark 审核失败原因
     */
    void auditIdPhoto(String username, Boolean validated, String handlerName, String remark);

    /**
     * 新增修改删除权限
     * @param permissionVO
     * @param handlerName
     */
    void savePermission(PermissionVO permissionVO, String handlerName);

    /**
     * 查询用户信息
     *
     * @param id username or email
     * @return
     */
    UserDTO getUserDTO(String id);

    /**
     * 返回用户列表
     * @param orgFlag
     * @param orgName
     * @param orgDeptId
     * @param orgDept
     * @param userType
     * @param valid
     * @param validStatus
     * @param keyword
     * @param pageable
     * @return
     */
    Page<UserDTO> queryUsers(String orgFlag, String orgName, Long orgDeptId, String orgDept, List<Integer> userType,Boolean valid,List<Integer> validStatus, String keyword, Pageable pageable);

    /**
     * 删除用户
     * @param username
     */
    void deleteUser(String username);

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
}
