package com.wd.cloud.uoserver.service;

import com.wd.cloud.commons.dto.UserDTO;
import com.wd.cloud.uoserver.entity.AuditUserInfo;
import com.wd.cloud.uoserver.entity.User;
import com.wd.cloud.uoserver.model.RegisterModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

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
     * 上传头像
     *
     * @param username
     * @param file
     */
    void uploadPhoto(String username, MultipartFile file);

    /**
     * 上传证件照
     *
     * @param username
     * @param file
     */
    void uploadIdPhoto(String username, MultipartFile file);

    /**
     * 审核验证证件照
     *
     * @param username
     */
    void auditIdPhoto(String username, Boolean validated);

    /**
     * 查询用户信息
     *
     * @param username
     * @return
     */
    UserDTO findByUsername(String username);

    /**
     * 注册新用户
     *
     * @param registerModel
     * @return
     */
    User register(RegisterModel registerModel);

    void give(String userName, String idPhoto, String nickName, String orgName, String department, Integer identity,
              String departmentId, Integer education, Short sex, String entranceTime, String email, Integer permission);


    AuditUserInfo getUserName(String userName);

    Page<AuditUserInfo> findAll(Pageable pageable, Map<String, Object> param);

    AuditUserInfo findById(Long id);

    void apply(Long id, Integer permission, String handlerName);

    void notApply(Long id, Integer permission, String handlerName);
}
