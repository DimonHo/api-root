package com.wd.cloud.bse.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hnlat.esmapping.annotation.Property;
import com.hnlat.esmapping.annotation.Property.FieldType;
import com.hnlat.esmapping.annotation.Property.IndexType;

public class Document {

	private String id;
	
	/**文档类型*/
	@Property(type=FieldType.INTEGER, index = IndexType.NOT_ANALYZED)
	private String docType;
	
	private List<Integer> docTypes;
	
	/**语言  1中文2英文*/
	@Property(type=FieldType.INTEGER, index = IndexType.NOT_ANALYZED)
	private Integer docLan;
	
	/**标题*/
	@Property(type=FieldType.TEXT, analyzer="mmseg", includeInAll=true, boost=12)
	private String docTitle;
	
	/**摘要*/
	@Property(type=FieldType.STRING, analyzer="mmseg", includeInAll=true)
	private String description;
	
	/**访问地址*/
	@Property(type=FieldType.OBJECT)
	private List<Url> url = new ArrayList<Url>();
	
	/**年份*/
	@Property(type=FieldType.INTEGER, index = IndexType.NOT_ANALYZED)
	private Integer year;
	
	/**关键词*/
	@Property(type=FieldType.TEXT, analyzer="mmseg", includeInAll=true, boost=9)
	private String keywords;
	
	/**作者*/
	@Property(type=FieldType.TEXT, analyzer="semicolon_spliter", includeInAll=true, boost = 4.5)
	private String author;
	
	@Property(type=FieldType.TEXT, analyzer="mmseg", boost = 4)
	private String authorAnalyzed;
	
	/**机构*/
	@Property(type=FieldType.TEXT, analyzer="mmseg", includeInAll=true, boost=4)
	private String org;
	
	/**导出题录*/
	private Map<String,Object> source;
	
	private List<String> linkUrl = new ArrayList<String>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getDocLan() {
		return docLan;
	}

	public void setDocLan(Integer docLan) {
		this.docLan = docLan;
	}

	public String getDocTitle() {
		return docTitle;
	}

	public void setDocTitle(String docTitle) {
		this.docTitle = docTitle;
	}

	public List<Url> getUrl() {
		return url;
	}

	public void setUrl(List<Url> url) {
		this.url = url;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getOrg() {
		return org;
	}

	public void setOrg(String org) {
		this.org = org;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
	
	/**机构签名*/
	@Property(index = IndexType.NOT_ANALYZED)
	private List<Integer> orgSign = new ArrayList<Integer>();
	
	/**院系*/
	@Property(index = IndexType.NOT_ANALYZED)
	private List<Integer> colleges = new ArrayList<Integer>();

	public List<Integer> getOrgSign() {
		return orgSign;
	}

	public void setOrgSign(List<Integer> orgSign) {
		this.orgSign = orgSign;
	}

	public List<Integer> getColleges() {
		return colleges;
	}

	public void setColleges(List<Integer> colleges) {
		this.colleges = colleges;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public List<Integer> getDocTypes() {
		return docTypes;
	}

	public void setDocTypes(List<Integer> docTypes) {
		this.docTypes = docTypes;
	}

	public String getAuthorAnalyzed() {
		return authorAnalyzed;
	}

	public void setAuthorAnalyzed(String authorAnalyzed) {
		this.authorAnalyzed = authorAnalyzed;
	}

	public Map<String, Object> getSource() {
		return source;
	}

	public void setSource(Map<String, Object> source) {
		this.source = source;
	}

	public List<String> getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(List<String> linkUrl) {
		this.linkUrl = linkUrl;
	}
	
}
