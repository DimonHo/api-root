package com.wd.cloud.fsserver;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@EnableJpaAuditing
@EnableSwagger2Doc
@SpringCloudApplication
public class FsServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FsServerApplication.class, args);
    }
}
