package com.wd.cloud.fsserver.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author He Zhigang
 * @date 2018/7/20
 * @Description:
 */
@Component
@ConfigurationProperties(value = "global")
public class GlobalConfig {

    private String rootPath;

    public String getRootPath() {
        return rootPath;
    }

    public GlobalConfig setRootPath(String rootPath) {
        this.rootPath = rootPath;
        return this;
    }
}
