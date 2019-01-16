package com.wd.cloud.authserver.controller;

import com.wd.cloud.commons.model.ResponseModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author He Zhigang
 * @date 2019/1/15
 * @Description:
 */
public class UserInfoController {

    @GetMapping("/user/{userId}")
    public ResponseModel getUser(@PathVariable Long userId){
        return ResponseModel.ok();
    }
}
