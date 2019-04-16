package com.wd.cloud.uoserver.listener;

import cn.hutool.crypto.SecureUtil;
import com.wd.cloud.uoserver.pojo.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.event.internal.DefaultLoadEventListener;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;

/**
 * @Author: He Zhigang
 * @Date: 2019/4/11 14:14
 * @Description: 用户密码加密监听
 */
@Slf4j
@Component
public class UserPasswordListener extends DefaultLoadEventListener implements PreUpdateEventListener {
    /**
     * user.password字段名称
     */
    private static final String PASSWORD = "password";

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @PostConstruct
    public void registerListeners() {
        log.info("register UserPasswordListener ******");
        SessionFactoryImpl sessionFactoryImpl = entityManagerFactory.unwrap(SessionFactoryImpl.class);
        EventListenerRegistry eventListenerRegistry = sessionFactoryImpl.getServiceRegistry().getService(EventListenerRegistry.class);
        eventListenerRegistry.getEventListenerGroup(EventType.PRE_UPDATE).appendListener(this);
    }

    @Override
    public boolean onPreUpdate(PreUpdateEvent preUpdateEvent) {
        if (preUpdateEvent.getEntity() instanceof User) {
            log.info("update USER start***********");
            User user = (User) preUpdateEvent.getEntity();
            for (int i = 0; i < preUpdateEvent.getPersister().getPropertyNames().length; i++) {
                // 如果password字段有修改，则更新之前先加密
                if (PASSWORD.equals(preUpdateEvent.getPersister().getPropertyNames()[i])
                        && preUpdateEvent.getOldState()[i] != preUpdateEvent.getState()[i]) {
                    log.info("旧密码：{}", preUpdateEvent.getOldState()[i].toString());
                    log.info("密码更新加密:  加密前：{}", user.getPassword());
                    String newPwd = SecureUtil.md5(user.getPassword());
                    // 注意此处直接user.setPassword()无效,但更新后可能需要返回加密的密码字段，所以这里set一下
                    user.setPassword(newPwd);
                    preUpdateEvent.getState()[i] = newPwd;
                    log.info("密码更新加密:  加密后：{}", newPwd);
                }
            }
            log.info("update USER end***********");
        }
        return false;
    }
}
