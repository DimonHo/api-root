package com.wd.cloud.reportanalysis;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;

@EnableSwagger2Doc
@SpringCloudApplication
public class ReportAnalysisApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReportAnalysisApplication.class, args);
    }
}
