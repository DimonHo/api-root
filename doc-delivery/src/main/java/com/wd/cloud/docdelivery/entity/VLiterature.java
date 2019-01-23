package com.wd.cloud.docdelivery.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * v_literature视图
 */
@Data
@Accessors(chain = true)
@Immutable
@Entity
@Table(name = "v_literature")
public class VLiterature {
    @Id
    private Long id;

    @Column(name = "doc_href")
    private String docHref;

    @Column(name = "doc_title")
    private String docTitle;

    private String unid;

    private String author;

    private String year;

    private String doi;

    private String summary;

    @Column(name = "is_reusing")
    private boolean reusing;

    @Column(name = "file_id")
    private String fileId;

    private Date gmtModified;

    private Date gmtCreate;
}
