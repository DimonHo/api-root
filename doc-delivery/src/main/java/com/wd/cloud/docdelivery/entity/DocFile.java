package com.wd.cloud.docdelivery.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.Objects;

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

    private String fileName;

    @ManyToOne
    @JsonIgnore
    private Literature literature;

    private Long auditorId;

    private String auditorName;

    /**
     * 审核状态，默认null,0:待审核，1为审核通过，2为审核不通过
     * 审核不通过和0的文件不可复用
     */
    @Column(name = "audit_status", columnDefinition = "tinyint default null COMMENT '0:待审核，1：审核通过，2：审核不通过'")
    private Integer auditStatus;
    /**
     * 复用
     */
    @Column(name = "is_reusing", columnDefinition = "tinyint(1) default 0 COMMENT '0:未复用，1：已复用'")
    private boolean reusing;

    /**
     * 备注
     */
    private String reMark;
}
