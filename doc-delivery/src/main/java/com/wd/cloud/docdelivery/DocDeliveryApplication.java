package com.wd.cloud.docdelivery;


import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * DocDeliveryApplication class
 *
 * @author hezhigang
 * @date 2018/04/08
 */
@EnableAsync
@EnableScheduling
@EnableJpaAuditing
@EnableSwagger2Doc
@EnableRedisHttpSession
@SpringCloudApplication
@EnableFeignClients(basePackages = {"com.wd.cloud.apifeign"})
public class DocDeliveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocDeliveryApplication.class, args);
    }

}
