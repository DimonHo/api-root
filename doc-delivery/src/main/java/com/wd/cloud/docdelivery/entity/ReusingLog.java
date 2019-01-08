package com.wd.cloud.docdelivery.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author He Zhigang
 * @date 2018/12/29
 * @Description:
 */
@Data
@Accessors(chain = true)
@Entity
@Table(name = "reusing_log")
public class ReusingLog extends AbstractEntity{

    private String handlerId;

    private String handlerName;

    /**
     * 0：取消复用，1：复用
     */
    private boolean reusing;

    private Long literatureId;

    private Long docFileId;
}
