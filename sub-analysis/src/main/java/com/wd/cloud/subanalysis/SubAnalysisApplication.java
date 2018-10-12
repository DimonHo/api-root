package com.wd.cloud.subanalysis;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;


@EnableSwagger2Doc
@SpringCloudApplication
public class SubAnalysisApplication {

    public static void main(String[] args) {
        SpringApplication.run(SubAnalysisApplication.class, args);
    }
}
