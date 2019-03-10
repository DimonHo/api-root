package com.wd.cloud.uoserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.system.UserInfo;
import com.wd.cloud.commons.dto.OrgDTO;
import com.wd.cloud.commons.dto.UserDTO;
import com.wd.cloud.commons.exception.FeignException;
import com.wd.cloud.commons.exception.NotFoundException;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.uoserver.constants.GlobalConstants;
import com.wd.cloud.uoserver.entity.AuditUserInfo;
import com.wd.cloud.uoserver.entity.Org;
import com.wd.cloud.uoserver.entity.User;
import com.wd.cloud.uoserver.enums.AuditEnums;
import com.wd.cloud.uoserver.exception.NotFoundOrgException;
import com.wd.cloud.uoserver.exception.NotFoundUserException;
import com.wd.cloud.uoserver.feign.FsServerApi;
import com.wd.cloud.uoserver.model.RegisterModel;
import com.wd.cloud.uoserver.repository.AuditUserInfoRepository;
import com.wd.cloud.uoserver.repository.OrgRepository;
import com.wd.cloud.uoserver.repository.UserRepository;
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
import java.util.Optional;

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
    AuditUserInfoRepository auditUserInfoRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    OrgRepository orgRepository;

    @Autowired
    FsServerApi fsServerApi;

    @Override
    public UserDTO buildUserInfo(Map<String, Object> authInfo) {
        UserDTO userDTO = BeanUtil.mapToBean(authInfo, UserDTO.class, true);
        // 如果用户有所属机构，则把有效机构设置为用户所属机构
        String spisFlag = (String) authInfo.get("school_flag");
        String eduFlag = (String) authInfo.get("edu_flag");
        if (spisFlag != null || eduFlag != null) {
            Org org = orgRepository.findByFlagOrSpisFlagOrEduFlag(spisFlag,spisFlag,eduFlag).orElseThrow(NotFoundOrgException::new);
            OrgDTO orgDTO = BeanUtil.toBean(org, OrgDTO.class);
            userDTO.setOrg(orgDTO);
        }
        User user = userRepository.findByUsername(userDTO.getUsername()).orElseThrow(NotFoundUserException::new);
        BeanUtil.copyProperties(user, userDTO);
        return userDTO;
    }

    @Override
    public void uploadPhoto(String username, MultipartFile file) {
        String headImg = uploadImage(file);
        User user = userRepository.findByUsername(username).orElse(new User());
        user.setUsername(username);
        user.setHeadImg(headImg);
        userRepository.save(user);
    }

    @Override
    public void uploadIdPhoto(String username, MultipartFile file) {
        String idPhoto = uploadImage(file);
        User user = userRepository.findByUsername(username).orElse(new User());
        user.setUsername(username);
        user.setIdPhoto(idPhoto);
        user.setValidated(false);
        userRepository.save(user);
    }

    private String uploadImage(MultipartFile file) {
        ResponseModel<JSONObject> responseModel = fsServerApi.uploadFile(GlobalConstants.UPLOAD_IMAGE_PATH, file);
        if (responseModel.isError()) {
            log.error("文件服务调用失败：{}", responseModel.getMessage());
            throw new FeignException("fsServer.uploadFile");
        }
        return responseModel.getBody().getStr("fileId");
    }


    @Override
    public void auditIdPhoto(String username, Boolean validated) {
        User user = userRepository.findByUsername(username).orElseThrow(NotFoundException::new);
        user.setValidated(validated);
        userRepository.save(user);
    }

    @Override
    public UserDTO findByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(NotFoundException::new);
        UserDTO userDTO = new UserDTO();
        BeanUtil.copyProperties(user, userDTO);
        if (user.getOrgId() != null) {
            Org org = orgRepository.findById(user.getOrgId()).orElseThrow(NotFoundException::new);
            OrgDTO orgDTO = new OrgDTO();
            BeanUtil.copyProperties(org, orgDTO);
            userDTO.setOrg(orgDTO);
        }
        return userDTO;
    }

    @Override
    public User register(RegisterModel registerModel) {
        User user = BeanUtil.toBean(registerModel, User.class);
        return userRepository.save(user);

    }

    @Override
    public List<Map<String,Object>> getUserInfoSchool(Map<String, Object> params) {
        List<Map<String, Object>> userInfoSchool = userRepository.getUserInfoSchool(params);
        return userInfoSchool;
    }



    @Override
    public void give(String userName, String idPhoto, String nickName, String orgName, String department, Integer identity,
                     String departmentId, Integer education, Short sex, String entranceTime,String email,Integer permission) {
        AuditUserInfo auditIdCard = new AuditUserInfo();
        auditIdCard.setUsername(userName);
        auditIdCard.setIdPhoto(idPhoto);
        auditIdCard.setNickName(nickName);
        auditIdCard.setOrgName(orgName);
        auditIdCard.setStatus(AuditEnums.WAITE.value());
        auditIdCard.setDepartment(department);
        auditIdCard.setDepartmentId(departmentId);
        auditIdCard.setIdentity(identity);
        auditIdCard.setEducation(education);
        auditIdCard.setSex(sex);
        auditIdCard.setEntranceTime(entranceTime);
        auditIdCard.setEmail(email);
        auditIdCard.setPermission(permission);
        auditUserInfoRepository.save(auditIdCard);
    }

    @Override
    public AuditUserInfo getUserName(String userName) {
        return auditUserInfoRepository.findByUsername(userName).orElseThrow(NotFoundException::new);
    }

    @Override
    public Page<AuditUserInfo> findAll(Pageable pageable, Map<String, Object> param) {
        Integer status = (Integer) param.get("status");
        String keyword = ((String) param.get("keyword"));
        keyword = keyword != null ? keyword.replaceAll("\\\\", "\\\\\\\\") : null;
        return auditUserInfoRepository.findAll(AuditUserInfoRepository.SpecificationBuilder.buildBackendList(status,keyword), pageable);
    }

    @Override
    public AuditUserInfo findById(Long id) {
        return auditUserInfoRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public Page<User> findUserAll(Pageable pageable, Map<String, Object> param) {
        String orgName = (String) param.get("orgName");
        String department = (String) param.get("department");
        String keyword = ((String) param.get("keyword"));
        keyword = keyword != null ? keyword.replaceAll("\\\\", "\\\\\\\\") : null;
        Page<User> userInfo = userRepository.findAll(UserRepository.SpecificationBuilder.buildFindUserInfoList(orgName, department, keyword), pageable);
        return userInfo;
    }


    @Override
    public void apply(Long id,Integer permission,String handlerName) {
        AuditUserInfo idCard = auditUserInfoRepository.findById(id).orElseThrow(NotFoundException::new);
        Date date = new Date();
        idCard.setHandleTime(date);
        idCard.setHandlerName(handlerName);
        idCard.setPermission(permission);
        idCard.setStatus(2);
        auditUserInfoRepository.save(idCard);

        User user = userRepository.findByUsername(idCard.getUsername()).orElseThrow(NotFoundException::new);
        user.setIdPhoto(idCard.getIdPhoto());
        user.setValidated(true);
        user.setNickname(idCard.getNickName());
        user.setDepartment(idCard.getDepartment());
        user.setDepartmentId(idCard.getDepartmentId());
        user.setPermission(permission);
        user.setSex(idCard.getSex());
        user.setIdentity(idCard.getIdentity());
        user.setEducation(idCard.getEducation());
        user.setEntranceTime(idCard.getEntranceTime());
        userRepository.save(user);
    }

    @Override
    public void notApply(Long id,Integer permission,String handlerName) {
        AuditUserInfo auditUserInfo = auditUserInfoRepository.findById(id).orElseThrow(NotFoundOrgException::new);
        Date date = new Date();
        auditUserInfo.setHandleTime(date);
        auditUserInfo.setHandlerName(handlerName);
        auditUserInfo.setPermission(permission);
        auditUserInfo.setStatus(1);
        auditUserInfoRepository.save(auditUserInfo);
    }

    @Override
    public User updateUser(Long id, String pwd, String nickName, String orgName, String department,
                                   Integer identity,String entranceTime, String departmentId, Integer education,
                                    Integer userType, Short sex, Integer permission,Long orgId) {
        User userId = userRepository.findById(id).orElse(null);
        userId.setPassword(pwd);
        userId.setNickname(nickName);
        userId.setOrgName(orgName);
        userId.setDepartment(department);
        userId.setIdentity(identity);
        userId.setEntranceTime(entranceTime);
        userId.setDepartmentId(departmentId);
        userId.setEducation(education);
        userId.setUserType(userType);
        userId.setSex(sex);
        userId.setPermission(permission);
        userId.setOrgId(orgId);
        User save = userRepository.save(userId);
        return save;
    }

    @Override
    public User findByUserId(Long id) {
        User user = userRepository.findById(id).orElse(null);
        return user;
    }

    @Override
    public User findByUserType(Long id ,Integer forbidden) {
        User user = userRepository.findById(id).orElse(null);
        user.setForbidden(forbidden);
        userRepository.save(user);
        return user;
    }

    @Override
    public void deleteUserId(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User userSave(User user) {
        User save = userRepository.save(user);
        return save;
    }

    @Override
    public User findByEmail(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        return user;
    }
}
