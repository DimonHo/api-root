package com.wd.cloud.zipkinserver;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import zipkin2.server.internal.EnableZipkinServer;


@EnableZipkinServer
@SpringCloudApplication
public class ZipkinServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZipkinServerApplication.class, args);
    }
}
