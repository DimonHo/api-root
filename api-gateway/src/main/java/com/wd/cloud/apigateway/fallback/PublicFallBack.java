package com.wd.cloud.apigateway.fallback;

import cn.hutool.json.JSONUtil;
import com.netflix.hystrix.exception.HystrixTimeoutException;
import com.wd.cloud.commons.exception.ApiException;
import com.wd.cloud.commons.model.ResponseModel;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Component
public class PublicFallBack implements FallbackProvider {

    @Override
    public String getRoute() {
        return "*";
    }

    @Override
    public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
        if (cause != null && cause.getMessage() != null) {
            log.info("【{}】服务调用异常： {}", route, cause.getMessage());
        }
        return buildResponse(route, cause);
    }

    private ClientHttpResponse buildResponse(String route, Throwable cause) {
        return new ClientHttpResponse() {
            @Override
            public HttpStatus getStatusCode() throws IOException {
                return HttpStatus.OK;
            }

            @Override
            public int getRawStatusCode() throws IOException {
                return this.getStatusCode().value();
            }

            @Override
            public String getStatusText() throws IOException {
                return this.getStatusCode().getReasonPhrase();
            }

            @Override
            public InputStream getBody() throws IOException {
                String message = "[" + route + "] " + cause.getMessage();
                ResponseModel responseModel = ResponseModel.fail().setMessage(message);
                if (cause instanceof HystrixTimeoutException) {
                    responseModel.setStatus(HttpStatus.GATEWAY_TIMEOUT.value());
                }  else {
                    responseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                }
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

            @Override
            public void close() {
            }
        };
    }
}
