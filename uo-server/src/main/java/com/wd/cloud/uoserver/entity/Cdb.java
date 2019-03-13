package com.wd.cloud.uoserver.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description: 馆藏数据库
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "cdb")
public class Cdb extends AbstractEntity {

    /**
     * 馆藏数据库名称
     */
    private String name;

    private String url;
}
