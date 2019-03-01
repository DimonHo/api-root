package com.wd.cloud.userserver;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * UserServerApplication class
 *
 * @author hezhigang
 * @date 2018/04/08
 */
@EnableSwagger2Doc
@EnableRedisHttpSession
@EnableJpaAuditing
@EnableFeignClients
@SpringCloudApplication
public class UserServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServerApplication.class, args);
    }
}
