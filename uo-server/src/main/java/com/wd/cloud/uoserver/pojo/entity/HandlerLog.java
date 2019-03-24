package com.wd.cloud.uoserver.pojo.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;


/**
 * @author He Zhigang
 * @date 2019/1/15
 * @Description: 操作日志
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "handler_log")
public class HandlerLog extends AbstractEntity {

    private String username;

    /**
     * 操作类型
     */
    private Integer type;

    /**
     * 操作人
     */
    @Column(name = "handler_name")
    private String handlerName;

    /**
     * 操作详情
     */
    private String remark;

}
