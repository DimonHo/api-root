package com.wd.cloud.bse.vo;

import java.io.Serializable;

public class ArticleSource implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7005712453622044901L;
	private String dbName;
	private String desc;
	private String url;
	private String ruleName;

	public ArticleSource() {
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ArticleSource){
			ArticleSource as = (ArticleSource)obj;
			if(this.url != null && this.url.equals(as.url)){
				return true;
			}
			return false;
		}else{
			return false;
		}
	}

	
}
