package com.wd.cloud.apigateway.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import com.wd.cloud.apigateway.feign.OrgServerApi;
import com.wd.cloud.apigateway.feign.UserServerApi;
import com.wd.cloud.apigateway.service.UserInfoService;
import com.wd.cloud.commons.dto.OrgDTO;
import com.wd.cloud.commons.dto.UserDTO;
import com.wd.cloud.commons.exception.NotFoundException;
import com.wd.cloud.commons.model.ResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.stereotype.Service;

import java.util.Map;


/**
 * @author He Zhigang
 * @date 2018/6/5
 * @Description:
 */
@Slf4j
@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    OrgServerApi orgServerApi;

    @Autowired
    UserServerApi userServerApi;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    RedisOperationsSessionRepository redisOperationsSessionRepository;

    @Override
    public UserDTO buildUserInfo(Map<String, Object> authInfo) {
        UserDTO userDTO = BeanUtil.mapToBean(authInfo, UserDTO.class, true);

        log.info("用户[{}]登陆成功", userDTO.getUsername());
        // 如果用户有所属机构，则把有效机构设置为用户所属机构
        Long orgId = (Long) authInfo.get("school_id");
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

    @Override
    public UserDTO buildUserInfo(String username, Long orgId, String type, String openId) {

        ResponseModel<JSONObject> userInfoResponse = userServerApi.getUserInfo(username);
        if (userInfoResponse.isError() || userInfoResponse.getBody() == null || userInfoResponse.getBody().get("username") == null) {
            throw new NotFoundException();
        }
        UserDTO userDTO = new UserDTO();
        BeanUtil.copyProperties(userInfoResponse.getBody(), userDTO);
        userDTO.setLoginType(type);
        switch (type) {
            case "qq":
                userDTO.setQqOpenid(openId);
                break;
            case "wechat":
                userDTO.setWechatOpenid(openId);
                break;
            case "weibo":
                userDTO.setWeiboOpenid(openId);
                break;
            default:
                break;
        }
        // 如果用户有所属机构，则把有效机构设置为用户所属机构
        if (orgId != null) {
            ResponseModel<OrgDTO> orgResponse = orgServerApi.getOrg(orgId);
            if (!orgResponse.isError()) {
                OrgDTO orgDTO = orgResponse.getBody();
                userDTO.setOrg(orgDTO);
            }
        }
        return userDTO;
    }
}
