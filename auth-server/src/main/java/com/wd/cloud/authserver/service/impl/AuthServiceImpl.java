package com.wd.cloud.authserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import com.wd.cloud.authserver.dto.UserInfoDTO;
import com.wd.cloud.authserver.exception.AuthException;
import com.wd.cloud.authserver.feign.SsoServerApi;
import com.wd.cloud.authserver.service.AuthService;
import com.wd.cloud.commons.model.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author He Zhigang
 * @date 2018/6/5
 * @Description:
 */
@Service("authService")
public class AuthServiceImpl implements AuthService {

    @Autowired
    SsoServerApi ssoServerApi;

    @Override
    public UserInfoDTO loing(String username, String pwd) {
        ResponseModel<JSONObject> responseModel = ssoServerApi.login(username, pwd);
        if (responseModel.isError()) {
            throw new AuthException(401, "用户名或密码错误");
        }
        JSONObject userJson = responseModel.getBody();
        UserInfoDTO userInfo = new UserInfoDTO();
        userInfo.setOrgId(userJson.getLong("school_id"));
        userInfo.setOrgName(userJson.getStr("school_name"));
        BeanUtil.copyProperties(responseModel.getBody(), userInfo);
        return userInfo;
    }
}
