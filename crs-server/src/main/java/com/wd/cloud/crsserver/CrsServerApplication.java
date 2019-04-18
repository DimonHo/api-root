package com.wd.cloud.crsserver;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@EnableSwagger2Doc
@SpringCloudApplication
public class CrsServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrsServerApplication.class, args);
    }

}
