package com.wd.cloud.pdfsearchserver;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;

@EnableSwagger2Doc
@SpringCloudApplication
public class PdfsearchServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PdfsearchServerApplication.class, args);
    }

}

