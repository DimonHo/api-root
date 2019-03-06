package com.wd.cloud.userserver.service;

import com.wd.cloud.commons.dto.UserDTO;
import com.wd.cloud.userserver.entity.UserInfo;
import com.wd.cloud.userserver.model.RegisterModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface UserInfoServer {
    void uploadIdPhoto( MultipartFile file);

    void validate(String username);

    UserDTO getUserInfo(String username);

    UserInfo register(RegisterModel registerModel);

    List<Map<String,Object>> getUserInfoSchool(Map<String, Object> params);

    Page<UserInfo> findAll(Pageable pageable, Map<String, Object> param);
}
