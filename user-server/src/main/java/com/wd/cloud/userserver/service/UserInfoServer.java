package com.wd.cloud.userserver.service;

import com.wd.cloud.userserver.entity.UserInfo;
import org.springframework.web.multipart.MultipartFile;

public interface UserInfoServer {
    UserInfo sava( MultipartFile file);

    void getValidated(Long userId);

    UserInfo getUserId(Long userId);
}
