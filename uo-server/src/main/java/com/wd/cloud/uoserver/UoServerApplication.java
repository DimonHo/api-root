package com.wd.cloud.uoserver;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


/**
 * UoServerApplication class
 *
 * @author hezhigang
 * @date 2019/03/04
 */
@RefreshScope
@EnableSwagger2Doc
@EnableRedisHttpSession
@EnableJpaAuditing
@EnableFeignClients
@SpringCloudApplication
public class UoServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(UoServerApplication.class, args);
    }

}
