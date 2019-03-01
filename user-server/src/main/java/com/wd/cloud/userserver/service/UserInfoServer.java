package com.wd.cloud.userserver.service;

import com.wd.cloud.commons.dto.UserDTO;
import com.wd.cloud.userserver.entity.UserInfo;
import com.wd.cloud.userserver.model.RegisterModel;
import org.springframework.web.multipart.MultipartFile;

public interface UserInfoServer {
    void uploadIdPhoto( MultipartFile file);

    void validate(String username);

    UserDTO getUserInfo(String username);

    UserInfo register(RegisterModel registerModel);
}
