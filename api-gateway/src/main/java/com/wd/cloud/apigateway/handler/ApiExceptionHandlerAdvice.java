package com.wd.cloud.apigateway.handler;

import com.wd.cloud.commons.exception.ApiException;
import com.wd.cloud.commons.model.ResponseModel;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * @author He Zhigang
 * @date 2018/12/25
 * @Description:
 */
@ControllerAdvice(annotations = RestController.class)
public class ApiExceptionHandlerAdvice {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseModel exception(Exception exception, HttpServletResponse response) {
        exception.printStackTrace();
        ResponseModel responseModel = ResponseModel.fail();
        //api异常
        if (exception instanceof ApiException) {
            responseModel.setMessage(exception.getMessage());
            responseModel.setStatus(((ApiException) exception).getStatus());
            responseModel.setBody(((ApiException) exception).getBody());
        } else {
            responseModel.setStatus(response.getStatus());
            responseModel.setMessage(exception.getMessage());
        }
        return responseModel;
    }
}
