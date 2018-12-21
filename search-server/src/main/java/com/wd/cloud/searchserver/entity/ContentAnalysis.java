package com.wd.cloud.searchserver.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * @author He Zhigang
 * @date 2018/12/19
 * @Description:
 */
@Entity
@Table(name = "t_content_analysis")
public class ContentAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String keyword;

    private String url;

    private Date time;

    private String batchId;
    private Integer systemId;

    private Integer systemType;

    private Integer memberId;

    private String orgFlag;
    private String db;
    private String method;
    private Integer type;

    public Long getId() {
        return id;
    }

    public ContentAnalysis setId(Long id) {
        this.id = id;
        return this;
    }

    public String getKeyword() {
        return keyword;
    }

    public ContentAnalysis setKeyword(String keyword) {
        this.keyword = keyword;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public ContentAnalysis setUrl(String url) {
        this.url = url;
        return this;
    }

    public Date getTime() {
        return time;
    }

    public ContentAnalysis setTime(Date time) {
        this.time = time;
        return this;
    }

    public String getBatchId() {
        return batchId;
    }

    public ContentAnalysis setBatchId(String batchId) {
        this.batchId = batchId;
        return this;
    }

    public Integer getSystemId() {
        return systemId;
    }

    public ContentAnalysis setSystemId(Integer systemId) {
        this.systemId = systemId;
        return this;
    }

    public Integer getSystemType() {
        return systemType;
    }

    public ContentAnalysis setSystemType(Integer systemType) {
        this.systemType = systemType;
        return this;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public ContentAnalysis setMemberId(Integer memberId) {
        this.memberId = memberId;
        return this;
    }

    public String getOrgFlag() {
        return orgFlag;
    }

    public ContentAnalysis setOrgFlag(String orgFlag) {
        this.orgFlag = orgFlag;
        return this;
    }

    public String getDb() {
        return db;
    }

    public ContentAnalysis setDb(String db) {
        this.db = db;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public ContentAnalysis setMethod(String method) {
        this.method = method;
        return this;
    }

    public Integer getType() {
        return type;
    }

    public ContentAnalysis setType(Integer type) {
        this.type = type;
        return this;
    }
}
