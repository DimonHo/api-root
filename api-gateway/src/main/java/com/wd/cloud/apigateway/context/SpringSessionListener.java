package com.wd.cloud.apigateway.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.events.SessionCreatedEvent;
import org.springframework.session.events.SessionDeletedEvent;
import org.springframework.session.events.SessionExpiredEvent;

/**
 * @author He Zhigang
 * @date 2019/1/19
 * @Description:
 */
@Slf4j
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 120)
public class SpringSessionListener {

    /**
     *      * Redis内session过期事件监听
     *      
     */
    @EventListener
    public void onSessionExpired(SessionExpiredEvent expiredEvent) {
        log.info("过期session:{}", expiredEvent.getSessionId());
    }


    /**
     *      * Redis内session删除事件监听
     *      
     */
    @EventListener
    public void onSessionDeleted(SessionDeletedEvent deletedEvent) {
        log.info("销毁session:{}", deletedEvent.getSessionId());
    }

    /**
     *      * Redis内session保存事件监听
     *      
     */
    @EventListener
    public void onSessionCreated(SessionCreatedEvent createdEvent) {
        log.info("创建session:{}", createdEvent.getSessionId());

    }
}