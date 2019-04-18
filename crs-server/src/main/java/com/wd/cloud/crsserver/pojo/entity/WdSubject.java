package com.wd.cloud.crsserver.pojo.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(callSuper = true)
@Entity
public class WdSubject extends AbstractEntity{

    /**
     * 学科名称
     */
    String nameCn;

    /**
     * 中英文名称互译
     */
    String nameEn;

    /**
     * 数据库类型： wos,esi,ssci,
     */
    String indexType;

    /**
     * 学科大类 数组类型 工学,理学
     */
    String subjectType;

    /**
     * 是否是中文学科
     */
    @Column(name = "is_cn" , columnDefinition = "bit(1) default 0")
    Boolean cn;
}
