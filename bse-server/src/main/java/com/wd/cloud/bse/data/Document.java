package com.wd.cloud.bse.data;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public abstract class Document {
	
	private String id;

	/**文档类型*/
	private List<Integer> docType;
	
	/**语言*/
	private Integer docLan;
	
	/**标题*/
	private String docTitle;
	
	/**摘要*/
	private String description;
	
	/**访问地址*/
	private List<Url> url = new ArrayList<Url>();
	
	/**年份*/
	private Integer year;
	
	/**关键词*/
	private String keywords;
	
	/**关键词(用于聚合查询)*/
	private List<String> keywordList;
	
	/**作者*/
	private String author;
	
	/**作者(用于聚合查询)*/
	private List<String> authorList;
	
	private String authorAnalyzed;
	
	/**机构*/
	private String org;
	
	/**第一作者*/
	private String firstAuthor;
	
	/**存储时间*/
	private Long storageTime;
	
	/**学校ID*/
	private Integer scid;
	
	/**提交用户ID*/
	private Integer userid;
	
	/**提交用户院系*/
	private Integer userCollege;
	
	/**来源类型*/
	private Integer sourceType;
	
	/**来源编码，如知网期刊为1,wos为400*/
	private Integer sourceCode;

	private Float myBoostField = 1.6f;
	
	/**机构签名*/
	private List<Integer> orgSign = new ArrayList<Integer>();
	
	/**院系*/
	private List<Integer> colleges = new ArrayList<Integer>();
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Document){
			if(this.id != null){
				return this.id.equals(((Document) obj).id);
			}
		}else if(obj instanceof Map){
			String _id = (String)((Map)obj).get("id");
			if(this.id != null){
				return this.id.equals(_id);
			}
		}
		return false;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Integer> getDocType() {
		return docType;
	}

	public void setDocType(List<Integer> docType) {
		this.docType = docType;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

//	public String getAuthor() {
//		return author;
//	}
	
	public String getAuthor() {
		return repeat(author, ";");
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

	public String getFirstAuthor() {
		return firstAuthor;
	}

	public void setFirstAuthor(String firstAuthor) {
		this.firstAuthor = firstAuthor;
	}

	public Long getStorageTime() {
		return storageTime;
	}

	public void setStorageTime(Long storageTime) {
		this.storageTime = storageTime;
	}

	public Integer getScid() {
		return scid;
	}

	public void setScid(Integer scid) {
		this.scid = scid;
	}

	public Integer getUserid() {
		return userid;
	}

	public void setUserid(Integer userid) {
		this.userid = userid;
	}

	public Integer getUserCollege() {
		return userCollege;
	}

	public void setUserCollege(Integer userCollege) {
		this.userCollege = userCollege;
	}

	public Integer getSourceType() {
		return sourceType;
	}

	public void setSourceType(Integer sourceType) {
		this.sourceType = sourceType;
	}

	public Integer getSourceCode() {
		return sourceCode;
	}

	public void setSourceCode(Integer sourceCode) {
		this.sourceCode = sourceCode;
	}

	public String getAuthorAnalyzed() {
		return authorAnalyzed;
	}

	public void setAuthorAnalyzed(String authorAnalyzed) {
		this.authorAnalyzed = authorAnalyzed;
	}
	
	public Float getMyBoostField() {
		return myBoostField;
	}

	public void setMyBoostField(Float myBoostField) {
		this.myBoostField = myBoostField;
	}

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
	
	public List<String> getKeywordList() {
		return keywordList;
	}

	public void setKeywordList(List<String> keywordList) {
		this.keywordList = keywordList;
	}

	public List<String> getAuthorList() {
		return authorList;
	}

	public void setAuthorList(List<String> authorList) {
		this.authorList = authorList;
	}

	protected Integer extractYear(String time) {
		Integer t = null;
		if(StringUtils.isNotBlank(time)&&time.length()>=4){
			try {
				t = Integer.parseInt(time.substring(0,4));
			} catch (Exception e) {
			}			
		}
		return t;
	}
	
	
	/**
	 * String数据去重
	 * @param val
	 * @return
	 */
	protected String repeat(String val,String split) {
		if(StringUtils.isEmpty(val)) {
			return val;
		}
		String[] vals = val.split(split);
		Map<String,Object> map = new HashMap<String,Object>();
		String value = "";
		for (String string : vals) {
			if(map.containsKey(string)) {
				
			} else {
				map.put(string, 1);
				value  = value + string.trim() + ";";
			}
		}
		
//		Set set = new HashSet();
//        //遍历数组并存入集合,如果元素已存在则不会重复存入
//        for (int i = 0; i < vals.length; i++) {
//            set.add(vals[i]);
//        }
//        Iterator<String> it = set.iterator(); 
//        String value = "";
//        while (it.hasNext()) {  
//        	String str = it.next();  
//        	value = value + str + ";";
//    	}  
		return value;
	}
	
	public abstract Document newInstance();
}
