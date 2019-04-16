package com.wd.cloud.crsserver.pojo.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @Author: He Zhigang
 * @Date: 2019/4/11 10:51
 * @Description: 学科表
 */
@Data
@Accessors(chain = true)
@Entity
public class Subject extends AbstractEntity{

    /**
     * 学科名称
     */
    String name;

    /**
     * 中英文名称互译
     */
    String translate;

    /**
     * 是否是中文
     */
    @Column(name = "is_cn" , columnDefinition = "bit(i) default 0")
    Boolean cn;
}
