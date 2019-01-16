package com.wd.cloud.orgserver;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @author He Zhigang
 * @date 2018/11/5
 * @Description:
 */
@EnableJpaAuditing
@EnableSwagger2Doc
@EnableRedisHttpSession(maxInactiveIntervalInSeconds= 3600,redisNamespace = "api")
@SpringCloudApplication
public class OrgServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrgServerApplication.class, args);
    }

}
