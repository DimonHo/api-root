package org.bse.server;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableSwagger2Doc
@EnableFeignClients(basePackages = {"com.wd.cloud.apifeign"})
@SpringCloudApplication
public class BseServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BseServerApplication.class, args);
    }
}
