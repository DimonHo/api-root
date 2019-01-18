package com.wd.cloud.authserver.service;

import com.wd.cloud.authserver.entity.UserInfo;
import org.springframework.web.multipart.MultipartFile;

public interface UserInfoServer {
    UserInfo sava( MultipartFile file);

    void getValidated(Long userId);

    UserInfo getUserId(Long userId);
}
