package com.wd.cloud.uoserver.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description: 产品表
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "product")
public class Product extends AbstractEntity{

    /**
     * 产品名称
     */
    private String name;

    /**
     * 产品官网url
     */
    private String url;
}
