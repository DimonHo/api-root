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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DocFile docFile = (DocFile) o;

        if (reusing != docFile.reusing) return false;
        if (fileId != null ? !fileId.equals(docFile.fileId) : docFile.fileId != null) return false;
        if (fileName != null ? !fileName.equals(docFile.fileName) : docFile.fileName != null) return false;
        if (auditorId != null ? !auditorId.equals(docFile.auditorId) : docFile.auditorId != null) return false;
        if (auditorName != null ? !auditorName.equals(docFile.auditorName) : docFile.auditorName != null) return false;
        if (auditStatus != null ? !auditStatus.equals(docFile.auditStatus) : docFile.auditStatus != null) return false;
        return reMark != null ? reMark.equals(docFile.reMark) : docFile.reMark == null;
    }

    @Override
    public int hashCode() {
        int result = fileId != null ? fileId.hashCode() : 0;
        result = 31 * result + (fileName != null ? fileName.hashCode() : 0);
        result = 31 * result + (auditorId != null ? auditorId.hashCode() : 0);
        result = 31 * result + (auditorName != null ? auditorName.hashCode() : 0);
        result = 31 * result + (auditStatus != null ? auditStatus.hashCode() : 0);
        result = 31 * result + (reusing ? 1 : 0);
        result = 31 * result + (reMark != null ? reMark.hashCode() : 0);
        return result;
    }
}
