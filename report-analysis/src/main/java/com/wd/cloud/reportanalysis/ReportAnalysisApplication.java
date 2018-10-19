package com.wd.cloud.reportanalysis;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import com.spring4all.swagger.EnableSwagger2Doc;


@EnableSwagger2Doc
@SpringCloudApplication
@EnableFeignClients(basePackages = {"com.wd.cloud.apifeign"})
public class ReportAnalysisApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReportAnalysisApplication.class, args);
	}
}
