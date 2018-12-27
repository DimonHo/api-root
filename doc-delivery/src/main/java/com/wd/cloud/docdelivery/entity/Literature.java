package com.wd.cloud.docdelivery.entity;

import cn.hutool.crypto.SecureUtil;
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
    @Column(name = "doc_href", length = 1000, columnDefinition = "default ''")
    private String docHref = "";
    /**
     * 文献标题
     */
    @NotNull
    @Column(name = "doc_title", length = 1000, columnDefinition = "default ''")
    private String docTitle = "";

    @Column(name = "unid")
    private String unid;
    /**
     * 文献作者
     */
    private String authors;

    /**
     * 发表年份
     */
    @Column(name = "year_of_publication")
    private String yearOfPublication;
    /**
     * doi
     */
    private String doi;
    /**
     * 摘要
     */
    private String summary;

    /**
     * 复用
     */
    @Column(name = "is_reusing", columnDefinition = "tinyint(1) COMMENT '0：未复用，1：已复用'")
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
}
