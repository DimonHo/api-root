package com.wd.cloud.bse.data;


import com.hnlat.esmapping.annotation.Property;
import com.hnlat.esmapping.annotation.Property.FieldType;
import com.hnlat.esmapping.annotation.Property.IndexType;

public class Proceedings extends Periodical {

    /**
     * 论文集
     */
    @Property(type = FieldType.TEXT, index = IndexType.ANALYZED, analyzer = "mmseg", boost = 7.4)
    private String corpus;

    /**
     * 会议日期(开始日期)
     */
    @Property(type = FieldType.TEXT, index = IndexType.NO)
    private String meetingDate;

    /**
     * 地址（会议地点）
     */
    @Property(index = IndexType.ANALYZED, analyzer = "mmseg")
    private String address;

    /**
     * 机构（会议主办单位）
     */
    @Property(index = IndexType.ANALYZED, analyzer = "mmseg")
    private String meetingOrg;

    /**
     * 会议名称
     */
    @Property(type = FieldType.TEXT, index = IndexType.ANALYZED, analyzer = "mmseg", boost = 7.4)
    private String meetingName;

    /**
     * 结束时间
     */
    @Property(index = IndexType.NO)
    private String endDate;

    /**
     * 出版地
     */
    @Property(index = IndexType.NO)
    private String pubAddr;

    /**
     * 出版者
     */
    @Property(index = IndexType.NO)
    private String publisher;

    /**
     * ISBN
     */
    @Property(index = IndexType.NO)
    private String isbn;

    /**
     * 会议赞助商
     */
    @Property(index = IndexType.NO)
    private String sponsor;

    /**
     * 主编
     */
    @Property(index = IndexType.NO)
    private String editor;

    public String getCorpus() {
        return corpus;
    }

    public void setCorpus(String corpus) {
        this.corpus = corpus;
    }

    public String getMeetingDate() {
        return meetingDate;
    }

    public void setMeetingDate(String meetingDate) {
        this.meetingDate = meetingDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMeetingOrg() {
        return meetingOrg;
    }

    public void setMeetingOrg(String meetingOrg) {
        this.meetingOrg = meetingOrg;
    }

    public String getMeetingName() {
        return meetingName;
    }

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getPubAddr() {
        return pubAddr;
    }

    public void setPubAddr(String pubAddr) {
        this.pubAddr = pubAddr;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getSponsor() {
        return sponsor;
    }

    public void setSponsor(String sponsor) {
        this.sponsor = sponsor;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

}
