package com.wd.cloud.docdelivery.entity;

import cn.hutool.crypto.SecureUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * @author He Zhigang
 * @date 2018/5/3
 * @Description: 文献元数据
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
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

//    @OneToMany(mappedBy = "literature")
//    @OrderBy(value = "gmt_create desc")
//    private Set<DocFile> docFiles;


    @PrePersist
    public void createUnid() {
        this.unid = SecureUtil.md5(this.docTitle + this.docHref);
    }

    @PreUpdate
    public void updateUnid() {
        this.unid = SecureUtil.md5(this.docTitle + this.docHref);
    }
}
