package com.wd.cloud.userserver.service;

import com.wd.cloud.userserver.entity.UserInfo;
import org.springframework.web.multipart.MultipartFile;

public interface UserInfoServer {
    void uploadIdPhoto( MultipartFile file);

    void validate(String username);

    UserInfo getUserInfo(String username);
}
