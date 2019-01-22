package com.wd.cloud.apigateway.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import com.wd.cloud.apigateway.feign.OrgServerApi;
import com.wd.cloud.apigateway.feign.SsoServerApi;
import com.wd.cloud.apigateway.feign.UserServerApi;
import com.wd.cloud.apigateway.service.AuthService;
import com.wd.cloud.commons.dto.OrgDTO;
import com.wd.cloud.commons.dto.UserDTO;
import com.wd.cloud.commons.exception.AuthException;
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

    @Autowired
    OrgServerApi orgServerApi;

    @Autowired
    UserServerApi userServerApi;

    @Override
    public UserDTO loing(String username, String pwd) {
        ResponseModel<JSONObject> ssoResponse = ssoServerApi.login(username, pwd);
        if (ssoResponse.isError()) {
            throw new AuthException("用户名或密码错误");
        }
        JSONObject userJson = ssoResponse.getBody();
        UserDTO userDTO = new UserDTO();
        BeanUtil.copyProperties(ssoResponse.getBody(), userDTO);
        Long orgId = userJson.getLong("school_id");
        if (orgId != null) {
            ResponseModel<OrgDTO> orgResponse = orgServerApi.getOrg(orgId);
            if (!orgResponse.isError()) {
                OrgDTO orgDTO = orgResponse.getBody();
                userDTO.setOrg(orgDTO);
            }
        }
        ResponseModel<JSONObject> userInfoResponse = userServerApi.getUserInfo(userDTO.getUsername());
        if (!userInfoResponse.isError()) {
            BeanUtil.copyProperties(userInfoResponse.getBody(), userDTO);
        }
        return userDTO;
    }
}
