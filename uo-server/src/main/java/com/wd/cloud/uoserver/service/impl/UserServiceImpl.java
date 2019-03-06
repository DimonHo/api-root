package com.wd.cloud.uoserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import com.wd.cloud.commons.dto.OrgDTO;
import com.wd.cloud.commons.dto.UserDTO;
import com.wd.cloud.commons.exception.FeignException;
import com.wd.cloud.commons.exception.NotFoundException;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.uoserver.constants.GlobalConstants;
import com.wd.cloud.uoserver.entity.Org;
import com.wd.cloud.uoserver.entity.User;
import com.wd.cloud.uoserver.exception.NotFoundOrgException;
import com.wd.cloud.uoserver.exception.NotFoundUserException;
import com.wd.cloud.uoserver.feign.FsServerApi;
import com.wd.cloud.uoserver.model.RegisterModel;
import com.wd.cloud.uoserver.repository.OrgRepository;
import com.wd.cloud.uoserver.repository.UserRepository;
import com.wd.cloud.uoserver.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description:
 */
@Slf4j
@Service("userService")
public class UserServiceImpl implements UserService {

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
}
