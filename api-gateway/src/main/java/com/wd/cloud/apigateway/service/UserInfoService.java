package com.wd.cloud.apigateway.service;

import com.wd.cloud.commons.dto.UserDTO;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author He Zhigang
 * @date 2019/1/19
 * @Description:
 */
public interface UserInfoService {

    UserDTO buildUserInfo(Map<String, Object> authInfo);

    UserDTO buildUserInfo(String username, Long orgId, String type, String openId);
}
