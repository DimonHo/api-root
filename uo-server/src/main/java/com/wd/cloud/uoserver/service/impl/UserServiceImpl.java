package com.wd.cloud.uoserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.wd.cloud.commons.dto.OrgDTO;
import com.wd.cloud.commons.dto.UserDTO;
import com.wd.cloud.commons.exception.FeignException;
import com.wd.cloud.commons.exception.NotFoundException;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.uoserver.constants.GlobalConstants;
import com.wd.cloud.uoserver.pojo.entity.*;
import com.wd.cloud.uoserver.enums.AuditEnum;
import com.wd.cloud.uoserver.enums.OutsideEnum;
import com.wd.cloud.uoserver.enums.PermissionTypeEnum;
import com.wd.cloud.uoserver.exception.NotFoundOrgException;
import com.wd.cloud.uoserver.exception.NotFoundUserException;
import com.wd.cloud.uoserver.feign.FsServerApi;
import com.wd.cloud.uoserver.pojo.vo.PerfectUserVO;
import com.wd.cloud.uoserver.pojo.vo.UserVO;
import com.wd.cloud.uoserver.repository.*;
import com.wd.cloud.uoserver.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description:
 */
@Slf4j
@Service("userService")
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PermissionRepository permissionRepository;

    @Autowired
    OrgRepository orgRepository;

    @Autowired
    FsServerApi fsServerApi;

    @Autowired
    AuditLogRepository auditLogRepository;

    @Autowired
    VUserRepository vUserRepository;

    @Autowired
    VUserAuditRepository vUserAuditRepository;

    @Override
    public UserDTO buildUserInfo(Map<String, Object> authInfo) {
        log.info("SSO认证中心用户对象： {}",MapUtil.join(authInfo,";","="));
        UserDTO userDTO = BeanUtil.mapToBean(authInfo, UserDTO.class, true);
        User user = userRepository.findUserById(userDTO.getUsername()).orElseThrow(NotFoundUserException::new);
        log.info("用户信息:{}",user);
        if (user.getOrgFlag()!= null){
            Org org = orgRepository.findByFlag(user.getOrgFlag()).orElseThrow(NotFoundOrgException::new);
            OrgDTO orgDTO = BeanUtil.toBean(org, OrgDTO.class);
            userDTO.setOrg(orgDTO);
        }
        BeanUtil.copyProperties(user, userDTO);
        log.info("用户DTO信息:{}",userDTO);
        return userDTO;
    }


    /**
     * 添加新用户
     * @param userVO
     * @return
     */
    @Override
    public User addUser(UserVO userVO) {
        User user = BeanUtil.toBean(userVO, User.class);
        return userRepository.save(user);

    }

    /**
     * 完善用户信息，获得6个月校外访问权限
     *
     * @param perfectUserVO
     * @return
     */
    @Override
    public User perfectUser(PerfectUserVO perfectUserVO) {
        User user = userRepository.findUserById(perfectUserVO.getUsername()).orElseThrow(NotFoundUserException::new);
        BeanUtil.copyProperties(perfectUserVO, user);
        // 完善信息自动获得6个月校外权限
        Permission permission = setOutsidePermission(user.getUsername(), OutsideEnum.HALF_YEAR, 6);
        permissionRepository.save(permission);
        user = userRepository.save(user);
        return user;
    }

    /**
     * 根据用户名或邮箱获取用户
     * @param id username or email
     * @return
     */
    @Override
    public UserDTO getUserDTO(String id) {
        User user = userRepository.findUserById(id).orElseThrow(NotFoundException::new);
        UserDTO userDTO = new UserDTO();
        BeanUtil.copyProperties(user, userDTO);
        if (StrUtil.isNotBlank(user.getOrgFlag())) {
            Org org = orgRepository.findByFlag(user.getOrgFlag()).orElseThrow(NotFoundException::new);
            OrgDTO orgDTO = new OrgDTO();
            BeanUtil.copyProperties(org, orgDTO);
            userDTO.setOrg(orgDTO);
        }
        return userDTO;
    }

    /**
     * 查询用户列表
     * @param orgFlag
     * @param orgName
     * @param departmentId
     * @param department
     * @param userType
     * @param keyword
     * @param pageable
     * @return
     */
    @Override
    public Page<VUser> getUsers(String orgFlag,String orgName,Long departmentId, String department,Integer userType, String keyword, Pageable pageable) {
        keyword = keyword != null ? keyword.replaceAll("\\\\", "\\\\\\\\") : null;
        return vUserRepository.findAll(VUserRepository.SpecBuilder.like(orgFlag,orgName,departmentId,department,userType,keyword),pageable);
    }

    /**
     * 上传头像
     * @param username
     * @param file
     */
    @Override
    public void uploadHeadImg(String username, MultipartFile file) {
        String headImg = uploadImage(file);
        User user = userRepository.findUserById(username).orElseThrow(NotFoundUserException::new);
        user.setHeadImg(headImg);
        userRepository.save(user);
    }

    /**
     * 上传证件照，添加待审核记录
     * @param username
     * @param file
     */
    @Override
    public void uploadIdPhoto(String username, MultipartFile file) {
        User user = userRepository.findUserById(username).orElseThrow(NotFoundUserException::new);
        String idPhoto = uploadImage(file);
        user.setIdPhoto(idPhoto);
        // 待审核
        user.setValidStatus(1);
        // 添加待审核记录
        AuditLog auditLog = auditLogRepository.findByUsernameAndStatus(username,AuditEnum.WAITE.value()).orElse(new AuditLog());
        auditLog.setStatus(AuditEnum.WAITE.value()).setIdPhoto(idPhoto).setUsername(username);
        auditLogRepository.save(auditLog);
        userRepository.save(user);
    }

    /**
     * 审核证件照
     *
     * @param username
     * @param validated
     * @param handlerName
     */
    @Override
    public void auditIdPhoto(String username, Boolean validated, String handlerName) {
        User user = userRepository.findUserById(username).orElseThrow(NotFoundUserException::new);
        if (validated) {
            // 验证通过后获得永久访问权限
            Permission permission = setOutsidePermission(username, OutsideEnum.FOREVER, 9999);
            permissionRepository.save(permission);
        }
        // 记录审核日志
        AuditLog auditLog = auditLogRepository.findByUsernameAndStatus(username,AuditEnum.WAITE.value()).orElseThrow(NotFoundException::new);
        auditLog.setStatus(validated?AuditEnum.PASS.value():AuditEnum.NO_PASS.value())
                .setHandlerName(handlerName);
        auditLogRepository.save(auditLog);
        // 已认证or未认证
        user.setValidStatus(validated ? 2 : 0);
        userRepository.save(user);
    }


    @Override
    public void deleteUser(String username) {
        userRepository.deleteById(username);
    }

    /**
     * 审核记录视图返回
     * @param status
     * @param keyword
     * @param pageable
     * @return
     */
    @Override
    public Page<VUserAudit> validList(Integer status, String keyword, Pageable pageable){
        return vUserAuditRepository.findAll(VUserAuditRepository.SpecBuilder.like(status,keyword),pageable);
    }


    @Override
    public List<Map<String, Object>> getUserInfoSchool(Map<String, Object> params) {
        List<Map<String, Object>> userInfoSchool = userRepository.getUserInfoSchool(params);
        return userInfoSchool;
    }


    @Override
    public List<Map<String, Object>> findByCountOrgName(String orgName) {
        List<Map<String, Object>> byCountOrgName = userRepository.findByCountOrgName(orgName);
        return byCountOrgName;
    }



    /**
     * 调用fs-server上传照片
     * @param file
     * @return
     */
    private String uploadImage(MultipartFile file) {
        ResponseModel<JSONObject> responseModel = fsServerApi.uploadFile(GlobalConstants.UPLOAD_IMAGE_PATH, file);
        if (responseModel.isError()) {
            log.error("文件服务调用失败：{}", responseModel.getMessage());
            throw new FeignException("fsServer.uploadFile");
        }
        return responseModel.getBody().getStr("fileId");
    }

    /**
     * 设置校外访问权限
     *
     * @param username
     * @param outsideEnum
     * @param offsetMonth
     * @return
     */
    private Permission setOutsidePermission(String username, OutsideEnum outsideEnum, Integer offsetMonth) {
        Permission permission = permissionRepository
                .findByUsernameAndType(username, PermissionTypeEnum.OUTSIDE.value())
                .orElse(new Permission());
        permission.setUsername(username)
                .setType(PermissionTypeEnum.OUTSIDE.value())
                .setValue(outsideEnum.value())
                .setEffDate(new Date())
                .setExpDate(DateUtil.offsetMonth(new Date(), offsetMonth));
        return permission;
    }
}
