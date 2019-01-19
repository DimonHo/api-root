package com.wd.cloud.authserver.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.authserver.entity.UserInfo;
import com.wd.cloud.commons.exception.AuthException;
import com.wd.cloud.authserver.feign.FsServerApi;
import com.wd.cloud.authserver.repository.UserInfoRepository;
import com.wd.cloud.authserver.service.UserInfoServer;
import com.wd.cloud.commons.constant.SessionConstant;
import com.wd.cloud.commons.dto.UserDTO;
import com.wd.cloud.commons.exception.FeignException;
import com.wd.cloud.commons.model.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;


@Service("userInfoService")
@Transactional(rollbackFor = Exception.class)
public class UserInfoServerImpl implements UserInfoServer {

    private static final Log log = LogFactory.get();

    @Autowired
    FsServerApi fsServerApi;

    @Autowired
    UserInfoRepository userInfoRepository;

    @Autowired
    HttpServletRequest request;

    @Override
    public UserInfo sava(MultipartFile file) {
        UserInfo userInfo = new UserInfo();
        UserDTO userDTO = (UserDTO) request.getSession().getAttribute(SessionConstant.LOGIN_USER);
        if (userDTO == null) {
            throw new AuthException(401, "请登陆后操作！");
        }
        Long useId = userDTO.getId();
        //保存文件
        try {
            String name = file.getName();
            ResponseModel<JSONObject> responseModel = fsServerApi.uploadFile(name, file);
            if (responseModel.isError()) {
                log.error("文件服务调用失败：{}", responseModel.getMessage());
                throw new FeignException("fsServer.uploadFile");
            }
            String idPhoto = responseModel.getBody().getStr("fileId");
            userInfo.setUserId(useId);
            userInfo.setIdPhoto(idPhoto);
            userInfo.setValidated(false);
            userInfoRepository.save(userInfo);
        } catch (Exception e) {
            log.error("文件保存失败!", e);
            e.printStackTrace();
        }
        return userInfo;
    }

    @Override
    public void getValidated(Long userId) {
        UserInfo userInfo = userInfoRepository.findByUserIdAndValidated(userId, false);
        if (userInfo == null) {
            throw new AuthException(401, "没有找到待审核的数据");
        }
        userInfo.setValidated(true);
        userInfoRepository.save(userInfo);
    }

    @Override
    public UserInfo getUserId(Long userId) {
        UserInfo userInfo = userInfoRepository.findUserId(userId);
        if (userInfo == null) {
            throw new AuthException(401, "没有找到对应的数据");
        }
        return userInfo;
    }
}
