package com.wd.cloud.uoserver.pojo.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Author: He Zhigang
 * @Date: 2019/3/23 13:31
 * @Description: 状态字典表
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "dic")
public class Dic extends AbstractEntity{
    /**
     *  表名
     */
    private String tableName;
    /**
     * 列名
     */
    private String columnName;
    /**
     * 字典名
     */
    private String name;
    /**
     * 字典值
     */
    private Integer value;
    /**
     * 备注
     */
    private String remark;
}
