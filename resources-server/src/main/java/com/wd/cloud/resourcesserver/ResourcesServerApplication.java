package com.wd.cloud.resourcesserver;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableSwagger2Doc
@EnableJpaAuditing
@SpringCloudApplication
public class ResourcesServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResourcesServerApplication.class, args);
    }
}
