package com.wd.cloud.uoserver.pojo.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Author: He Zhigang
 * @Date: 2019/3/23 19:41
 * @Description: 用户消息
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_msg")
public class UserMsg extends AbstractEntity{

    /**
     * 用户
     */
    private String username;

    /**
     * 消息内容
     */
    private String msg;

    /**
     * 是否已读
     */
    @Column(name = "is_read",columnDefinition = "bit(1) default 0 COMMENT '0:未读，1：已读'")
    private boolean read;
}
