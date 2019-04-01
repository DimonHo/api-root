package com.wd.cloud.uoserver.handler;

import com.wd.cloud.commons.exception.ApiException;
import com.wd.cloud.commons.model.ResponseModel;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@ControllerAdvice(annotations = RestController.class)
public class ApiExceptionHandlerAdvice {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseModel exception(Exception exception, HttpServletResponse response) {
        ResponseModel responseModel = ResponseModel.fail().setMessage(exception.getMessage());
        //api异常
        if (exception instanceof ApiException) {
            responseModel.setStatus(((ApiException) exception).getStatus());
            responseModel.setBody(((ApiException) exception).getBody());
        }
        exception.printStackTrace();
        return responseModel;
    }
}
