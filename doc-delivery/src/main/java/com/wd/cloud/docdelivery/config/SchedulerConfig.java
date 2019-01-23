package com.wd.cloud.docdelivery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @author He Zhigang
 * @date 2019/1/23
 * @Description:
 */
@Configuration
public class SchedulerConfig {

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        //线程池大小
        scheduler.setPoolSize(10);
        //线程名字前缀
        scheduler.setThreadNamePrefix("spring-task-thread");
        return scheduler;
    }
}
