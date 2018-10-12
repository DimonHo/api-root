package com.wd.cloud.orgserver;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;

@EnableSwagger2Doc
@SpringCloudApplication
public class OrgServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrgServerApplication.class, args);
    }
}
