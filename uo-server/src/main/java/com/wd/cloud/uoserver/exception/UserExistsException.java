package com.wd.cloud.uoserver.exception;

import com.wd.cloud.commons.exception.ApiException;

/**
 * @Author: He Zhigang
 * @Date: 2019/3/20 17:34
 * @Description:
 */
public class UserExistsException extends ApiException {

    public UserExistsException(Integer status, String message) {
        super(status, message);
    }

    public static UserExistsException userExists(String username) {
        String message = String.format("用户名[%d]已存在", username);
        return new UserExistsException(ExceptionStatus.EXISTS_USER, message);
    }

    public static UserExistsException emailExists(String email) {
        String message = String.format("邮箱[%d]已被占用", email);
        return new UserExistsException(ExceptionStatus.EXISTS_EMAIL, message);
    }

}
