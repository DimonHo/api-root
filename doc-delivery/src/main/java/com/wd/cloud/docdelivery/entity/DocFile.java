package com.wd.cloud.docdelivery.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author He Zhigang
 * @date 2018/5/27
 * @Description:
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "doc_file",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"literature_id", "file_id"})})
public class DocFile extends AbstractEntity {

    @Column(name = "file_id")
    private String fileId;

    @Column(name = "literature_id")
    private Long literatureId;

    /**
     * 复用
     */
    @Column(name = "is_reusing", columnDefinition = "tinyint(1) default 0 COMMENT '0:未复用，1：已复用'")
    private boolean reusing;

    private String handlerName;

}
