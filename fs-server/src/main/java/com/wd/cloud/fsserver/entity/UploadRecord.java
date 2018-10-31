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
@Table(name = "upload_record", uniqueConstraints = {@UniqueConstraint(columnNames = {"unid"})})
public class UploadRecord extends AbstractEntity {

    @NotNull
    @Column(name = "unid", length = 64)
    private String unid;

    /**
     * 保存路径,如果是保存在hbase，path表示tablename
     */
    private String path;
    /**
     * 文件名称
     */
    @Column(name = "file_name", length = 1000)
    private String fileName;

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

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isMissed() {
        return missed;
    }

    public void setMissed(boolean missed) {
        this.missed = missed;
    }

    public boolean isAsynced() {
        return asynced;
    }

    public void setAsynced(boolean asynced) {
        this.asynced = asynced;
    }

    public String getUnid() {
        return unid;
    }

    public void setUnid(String unid) {
        this.unid = unid;
    }

    /**
     * 数据插入之前，自动计算设置unid字段值
     */
    @PrePersist
    public void createUnid() {
        this.unid = FileUtil.buildFileUuid(this.path, this.md5);
    }

    /**
     * 数据更新之前，自动计算设置unid字段值
     */
    @PreUpdate
    public void updateUnid() {
        this.unid = FileUtil.buildFileUuid(this.path, this.md5);
    }
}
