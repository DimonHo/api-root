package com.wd.cloud.bse.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean(name = "xkDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.xk")
    public DataSource fristDataSource() {
        System.out.println("-------------------- fristDataSource init ---------------------");
        return new DruidDataSource();
    }

    @Bean(name = "schoolDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.school")
    public DataSource secondaryDataSource() {
        System.out.println("-------------------- secondaryDataSource init ---------------------");
        return new DruidDataSource();
    }

}
