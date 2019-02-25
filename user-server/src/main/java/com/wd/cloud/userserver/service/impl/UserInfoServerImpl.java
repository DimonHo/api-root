package com.wd.cloud.userserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import com.wd.cloud.commons.constant.SessionConstant;
import com.wd.cloud.commons.dto.OrgDTO;
import com.wd.cloud.commons.dto.UserDTO;
import com.wd.cloud.commons.exception.AuthException;
import com.wd.cloud.commons.exception.FeignException;
import com.wd.cloud.commons.exception.NotFoundException;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.userserver.constants.GlobalConstants;
import com.wd.cloud.userserver.entity.UserInfo;
import com.wd.cloud.userserver.feign.FsServerApi;
import com.wd.cloud.userserver.feign.OrgServerApi;
import com.wd.cloud.userserver.repository.UserInfoRepository;
import com.wd.cloud.userserver.service.UserInfoServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Slf4j
@Service("userInfoService")
@Transactional(rollbackFor = Exception.class)
public class UserInfoServerImpl implements UserInfoServer {

    @Autowired
    FsServerApi fsServerApi;

    @Autowired
    OrgServerApi orgServerApi;

    @Autowired
    UserInfoRepository userInfoRepository;

    @Autowired
    HttpServletRequest request;


    @Override
    public void uploadIdPhoto(MultipartFile file) {

        UserDTO userDTO = (UserDTO) request.getSession().getAttribute(SessionConstant.LOGIN_USER);
        if (userDTO == null) {
            throw new AuthException();
        }
        ResponseModel<JSONObject> responseModel = fsServerApi.uploadFile(GlobalConstants.UPLOAD_IMAGE_PATH, file);
        if (responseModel.isError()) {
            log.error("文件服务调用失败：{}", responseModel.getMessage());
            throw new FeignException("fsServer.uploadFile");
        }
        String idPhoto = responseModel.getBody().getStr("fileId");
        UserInfo userInfo = userInfoRepository.findByUsername(userDTO.getUsername()).orElse(new UserInfo());
        userInfo.setUsername(userDTO.getUsername());
        userInfo.setIdPhoto(GlobalConstants.UPLOAD_IMAGE_PATH + "/" + idPhoto);
        userInfo.setValidated(false);
        userInfoRepository.save(userInfo);
    }

    @Override
    public void validate(String username) {
        UserInfo userInfo = userInfoRepository.findByUsernameAndValidated(username, false);
        if (userInfo == null) {
            throw new AuthException();
        }
        userInfo.setValidated(true);
        userInfoRepository.save(userInfo);
    }

    @Override
    public UserDTO getUserInfo(String username) {
        Optional<UserInfo> optionalUserInfo = userInfoRepository.findByUsername(username);
        optionalUserInfo.orElseThrow(NotFoundException::new);
        ResponseModel<OrgDTO> orgResponse = orgServerApi.getOrg(optionalUserInfo.get().getOrgId());
        UserDTO userDTO = new UserDTO();
        BeanUtil.copyProperties(optionalUserInfo.get(), userDTO);
        if (!orgResponse.isError()) {
            userDTO.setOrg(orgResponse.getBody());
        }
        return userDTO;
    }
}
