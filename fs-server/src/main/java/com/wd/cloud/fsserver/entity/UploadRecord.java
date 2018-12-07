package com.wd.cloud.fsserver.entity;


import com.wd.cloud.fsserver.util.FileUtil;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * @author He Zhigang
 * @date 2018/9/3
 * @Description:
 */
@Entity
@Table(name = "upload_record", uniqueConstraints = {@UniqueConstraint(columnNames = {"unid"}),
        @UniqueConstraint(columnNames = {"path", "md5"})})
public class UploadRecord extends AbstractEntity {

    @NotNull
    @Column(name = "unid", length = 64)
    private String unid;

    /**
     * 保存路径,如果是保存在hbase，path表示tablename
     */
    private String path;
    /**
     * 文件MD5的文件名
     */
    @Column(name = "file_name", length = 1000)
    private String fileName;

    /**
     * 源文件名称
     */
    private String srcName;

    private String md5;
    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件是否丢失，默认false
     */
    @Column(name = "is_missed", columnDefinition = "tinyint(1) default 0")
    private boolean missed;

    /**
     * 是否已经同步到hbase,默认false
     */
    @Column(name = "is_asynced", columnDefinition = "tinyint(1) default 0")
    private boolean asynced;

    public String getUnid() {
        return unid;
    }

    public UploadRecord setUnid(String unid) {
        this.unid = unid;
        return this;
    }

    public String getSrcName() {
        return srcName;
    }

    public UploadRecord setSrcName(String srcName) {
        this.srcName = srcName;
        return this;
    }

    public String getPath() {
        return path;
    }

    public UploadRecord setPath(String path) {
        this.path = path;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public UploadRecord setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public String getMd5() {
        return md5;
    }

    public UploadRecord setMd5(String md5) {
        this.md5 = md5;
        return this;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public UploadRecord setFileSize(Long fileSize) {
        this.fileSize = fileSize;
        return this;
    }

    public String getFileType() {
        return fileType;
    }

    public UploadRecord setFileType(String fileType) {
        this.fileType = fileType;
        return this;
    }

    public boolean isMissed() {
        return missed;
    }

    public UploadRecord setMissed(boolean missed) {
        this.missed = missed;
        return this;
    }

    public boolean isAsynced() {
        return asynced;
    }

    public UploadRecord setAsynced(boolean asynced) {
        this.asynced = asynced;
        return this;
    }

    /**
     * 数据插入之前，自动计算设置unid字段值
     */
    @PrePersist
    public void createUnid() {
        this.unid = FileUtil.buildFileUnid(this.path, this.md5);
    }

    /**
     * 数据更新之前，自动计算设置unid字段值
     */
    @PreUpdate
    public void updateUnid() {
        this.unid = FileUtil.buildFileUnid(this.path, this.md5);
    }
}
