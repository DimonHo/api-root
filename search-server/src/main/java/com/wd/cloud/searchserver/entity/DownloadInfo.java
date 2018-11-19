package com.wd.cloud.searchserver.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
public class DownloadInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;

    private String username;

    private String school;

    private String schoolFlag;

    private String ip;

    private Date time;

    private int downloadId;

    private int num;

    private int type; //0原始数据，1添加数据

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getSchoolFlag() {
        return schoolFlag;
    }

    public void setSchoolFlag(String schoolFlag) {
        this.schoolFlag = schoolFlag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    /**
     * 用于a链接里的引号特殊处理
     * @return
     */
    public String getTitles() {
        return title.replace("\"", "$034");
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(int downloadId) {
        this.downloadId = downloadId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
