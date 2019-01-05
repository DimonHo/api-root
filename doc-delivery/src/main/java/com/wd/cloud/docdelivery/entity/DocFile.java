package com.wd.cloud.docdelivery.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * @author He Zhigang
 * @date 2018/5/27
 * @Description:
 */
@Data
@Accessors(chain = true)
@Entity
@Table(name = "doc_file",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"literature_id", "file_id"})})
public class DocFile extends AbstractEntity {

    @Column(name = "file_id")
    private String fileId;

    @ManyToOne
    @JsonIgnore
    private Literature literature;

    /**
     * 复用
     */
    @Column(name = "is_reusing", columnDefinition = "tinyint(1) default 0 COMMENT '0:未复用，1：已复用'")
    private boolean reusing;

}
