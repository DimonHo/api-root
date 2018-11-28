package org.bse.server.data;



public class Proceedings extends Periodical{
	
	/**论文集*/
	private String corpus;
	
	/**会议日期(开始日期)*/
	private String meetingDate;
	
	/**地址（会议地点）*/
	private String address;

	/**机构（会议主办单位）*/
	private String meetingOrg;
	
	/**会议名称*/
	private String meetingName;
	
	/**主编*/
	private String editor;
	
	/**结束时间*/
	private String endDate;
	
	/**会议赞助商*/
	private String sponsor;
	
	/**出版地*/
	private String pubAddr;
	
	/**出版者*/
	private String publisher;
	
	/**ISBN*/
	private String isbn;

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

	public String getEditor() {
		return editor;
	}

	public void setEditor(String editor) {
		this.editor = editor;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getSponsor() {
		return sponsor;
	}

	public void setSponsor(String sponsor) {
		this.sponsor = sponsor;
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

	@Override
	public Document newInstance() {
		return new Proceedings();
	}
	
}
