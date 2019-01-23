package com.wd.cloud.docdelivery.entity;

import cn.hutool.crypto.SecureUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * @author He Zhigang
 * @date 2018/5/3
 * @Description: 文献元数据
 */
@Data
@Accessors(chain = true)
@Entity
@Table(name = "literature", uniqueConstraints = {@UniqueConstraint(columnNames = {"unid"})})
public class Literature extends AbstractEntity {

    /**
     * 文献的链接地址
     */
    @Column(name = "doc_href", length = 1000)
    private String docHref;
    /**
     * 文献标题
     */
    @NotNull
    @Column(name = "doc_title", length = 1000)
    private String docTitle;

    @Column(name = "unid")
    private String unid;

    private String journal;
    /**
     * doi
     */
    private String doi;
    /**
     * 摘要
     */
    private String summary;

    private String issn;

    private String issue;

    private String volume;

    /**
     * 发表年份
     */
    private String year;

    /**
     * 文献作者
     */
    private String author;

    /**
     * 复用
     */
    @Column(name = "is_reusing", columnDefinition = "tinyint(1) default 0 COMMENT '0:未复用，1：已复用'")
    private boolean reusing;

    @OneToMany(mappedBy = "literature")
    @OrderBy(value = "gmt_create desc")
    @Where(clause = "audit_status not in (0,2) or audit_status is null")
    private Set<DocFile> docFiles;

    @PrePersist
    public void createUnid() {
        this.unid = SecureUtil.md5(this.docTitle + this.docHref);
    }

    @PreUpdate
    public void updateUnid() {
        this.unid = SecureUtil.md5(this.docTitle + this.docHref);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Literature that = (Literature) o;

        if (reusing != that.reusing) return false;
        if (docHref != null ? !docHref.equals(that.docHref) : that.docHref != null) return false;
        if (docTitle != null ? !docTitle.equals(that.docTitle) : that.docTitle != null) return false;
        if (unid != null ? !unid.equals(that.unid) : that.unid != null) return false;
        if (journal != null ? !journal.equals(that.journal) : that.journal != null) return false;
        if (doi != null ? !doi.equals(that.doi) : that.doi != null) return false;
        if (summary != null ? !summary.equals(that.summary) : that.summary != null) return false;
        if (issn != null ? !issn.equals(that.issn) : that.issn != null) return false;
        if (issue != null ? !issue.equals(that.issue) : that.issue != null) return false;
        if (volume != null ? !volume.equals(that.volume) : that.volume != null) return false;
        if (year != null ? !year.equals(that.year) : that.year != null) return false;
        return author != null ? author.equals(that.author) : that.author == null;
    }

    @Override
    public int hashCode() {
        int result = docHref != null ? docHref.hashCode() : 0;
        result = 31 * result + (docTitle != null ? docTitle.hashCode() : 0);
        result = 31 * result + (unid != null ? unid.hashCode() : 0);
        result = 31 * result + (journal != null ? journal.hashCode() : 0);
        result = 31 * result + (doi != null ? doi.hashCode() : 0);
        result = 31 * result + (summary != null ? summary.hashCode() : 0);
        result = 31 * result + (issn != null ? issn.hashCode() : 0);
        result = 31 * result + (issue != null ? issue.hashCode() : 0);
        result = 31 * result + (volume != null ? volume.hashCode() : 0);
        result = 31 * result + (year != null ? year.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (reusing ? 1 : 0);
        return result;
    }
}
