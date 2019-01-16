package com.wd.cloud.authserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.netflix.discovery.converters.Auto;
import com.wd.cloud.authserver.exception.AuthException;
import com.wd.cloud.authserver.feign.OrgServerApi;
import com.wd.cloud.authserver.feign.SsoServerApi;
import com.wd.cloud.authserver.service.AuthService;
import com.wd.cloud.commons.dto.OrgDTO;
import com.wd.cloud.commons.dto.UserDTO;
import com.wd.cloud.commons.model.ResponseModel;
import lombok.experimental.Accessors;
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

    @Override
    public UserDTO loing(String username, String pwd) {
        ResponseModel<JSONObject> ssoResponse = ssoServerApi.login(username, pwd);
        if (ssoResponse.isError()) {
            throw new AuthException(401, "用户名或密码错误");
        }
        JSONObject userJson = ssoResponse.getBody();
        UserDTO userInfo = new UserDTO();
        BeanUtil.copyProperties(ssoResponse.getBody(), userInfo);
        Long orgId = userJson.getLong("school_id");
        if (orgId != null){
            ResponseModel<JSONObject> orgResponse = orgServerApi.getOrg(orgId);
            if (!orgResponse.isError()){
                OrgDTO orgDTO = JSONUtil.toBean(orgResponse.getBody(),OrgDTO.class);
                userInfo.setOrg(orgDTO);
            }
        }

        return userInfo;
    }
}
