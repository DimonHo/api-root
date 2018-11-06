package com.wd.cloud.wdtjserver.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
@Component
public class AutoTask {

    /**
     * 每天凌晨0点执行一次
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void auto(){

    }
}
