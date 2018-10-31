package com.wd.cloud.apigateway.fallback;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.netflix.hystrix.exception.HystrixTimeoutException;
import com.wd.cloud.commons.enums.StatusEnum;
import com.wd.cloud.commons.model.ResponseModel;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author He Zhigang
 * @date 2018/10/17
 * @Description:
 */
@Component
public class PublicFallBack implements FallbackProvider {
    private static final Log log = LogFactory.get();

    @Override
    public String getRoute() {
        return "*";
    }

    @Override
    public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
        if (cause != null && cause.getMessage() != null) {
            String reason = cause.getMessage();
            log.info("调用异常： {}", reason);
        }
        if (cause instanceof HystrixTimeoutException) {
            return fallbackResponse(HttpStatus.GATEWAY_TIMEOUT, cause);
        } else {
            return fallbackResponse(HttpStatus.INTERNAL_SERVER_ERROR, cause);
        }

    }

    private ClientHttpResponse fallbackResponse(HttpStatus status, Throwable cause) {
        return new ClientHttpResponse() {
            @Override
            public HttpStatus getStatusCode() throws IOException {
                return status;
            }

            @Override
            public int getRawStatusCode() throws IOException {
                return status.value();
            }

            @Override
            public String getStatusText() throws IOException {
                return status.getReasonPhrase();
            }

            @Override
            public void close() {

            }

            @Override
            public InputStream getBody() throws IOException {

                ResponseModel responseModel = ResponseModel.fail()
                        .setStatus(StatusEnum.FALL_BACK.value())
                        .setMessage(cause.getMessage());
                //返回前端的内容
                return new ByteArrayInputStream(JSONUtil.toJsonStr(responseModel).getBytes());
            }

            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders httpHeaders = new HttpHeaders();
                //设置头
                httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
                return httpHeaders;
            }
        };
    }
}
