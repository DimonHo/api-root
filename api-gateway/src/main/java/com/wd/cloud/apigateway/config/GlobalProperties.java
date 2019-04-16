package com.wd.cloud.apigateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: He Zhigang
 * @Date: 2019/3/24 23:11
 * @Description:
 */
@Data
@Component
@ConfigurationProperties(prefix = "global", ignoreUnknownFields = false)
public class GlobalProperties {

    private String cookieDomain;
}
