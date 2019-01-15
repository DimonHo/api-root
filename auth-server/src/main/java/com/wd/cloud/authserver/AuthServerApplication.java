package com.wd.cloud.authserver;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * AuthServerApplication class
 *
 * @author hezhigang
 * @date 2018/04/08
 */
@EnableSwagger2Doc
@EnableRedisHttpSession(maxInactiveIntervalInSeconds= 3600,redisNamespace = "api")
@EnableFeignClients
@SpringCloudApplication
public class AuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServerApplication.class, args);
    }
}
