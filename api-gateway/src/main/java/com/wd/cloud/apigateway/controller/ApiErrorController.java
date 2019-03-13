package com.wd.cloud.apigateway.controller;

import com.wd.cloud.commons.model.ResponseModel;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author He Zhigang
 * @date 2019/3/5
 * @Description:
 */
@RestController
public class ApiErrorController implements ErrorController {

    @Override
    public String getErrorPath() {
        return "/error";
    }

    @RequestMapping("/error")
    public ResponseModel error() {
        return ResponseModel.fail();
    }
}
