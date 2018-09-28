package com.wd.cloud.subanalysis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author He Zhigang
 * @date 2018/9/26
 * @Description:
 */
@Component
@ConfigurationProperties(value = "customer")
public class CustomerConfig {

    private ElasticVar es;

    @Bean(value = "es")
    public ElasticVar getEs() {
        return es;
    }

    public void setEs(ElasticVar es) {
        this.es = es;
    }
}
