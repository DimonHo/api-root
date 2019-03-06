package com.wd.cloud.uoserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 * @author He Zhigang
 * @date 2019/2/22
 * @Description:
 */
@Data
@ConfigurationProperties(value = "cas")
public class CasProperties implements Serializable {
    private String clientHostUrl;

    private String serverUrlPrefix;

    private String serverLoginUrl;

    private String logoutUrlPatterns;

    private String authenticationUrlPatterns;

    private String validationUrlPatterns;

    private String requestWrapperUrlPatterns;
}