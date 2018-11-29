package com.wd.cloud.bse.data;

import java.util.List;

/**
 * 学位论文
 * @author Administrator
 *
 */
public class Dissertation extends Document{
	
	/**导师*/
	private String reprintAuthor;
	
	/**导师(用于聚合查询)*/
	private List<String> reprintAuthorList;
	
	/**导师所属机构*/
	private String reprintAuthorOrg;
	
	private String doi;
	
	private String degree;//学位
	
	/**学位授予单位*/
	private String degreeAwarder;
	
	/**学科专业*/
//	@Property(analyzer="mmseg", boost=4.9) 2018-08-02
	private String researchFields;
	
	/**分类号*/
	private String subjectClc;
	
	/**参考文献*/
	private String references;
	
	public String getReprintAuthor() {
		return reprintAuthor;
	}

	public void setReprintAuthor(String reprintAuthor) {
		this.reprintAuthor = reprintAuthor;
	}

	public String getReprintAuthorOrg() {
		return reprintAuthorOrg;
	}

	public void setReprintAuthorOrg(String reprintAuthorOrg) {
		this.reprintAuthorOrg = reprintAuthorOrg;
	}

	public String getDoi() {
		return doi;
	}

	public void setDoi(String doi) {
		this.doi = doi;
	}

	public String getDegree() {
		return degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}

	public String getDegreeAwarder() {
		return degreeAwarder;
	}

	public void setDegreeAwarder(String degreeAwarder) {
		this.degreeAwarder = degreeAwarder;
	}

	public String getResearchFields() {
		return researchFields;
	}

	public void setResearchFields(String researchFields) {
		this.researchFields = researchFields;
	}

	public String getSubjectClc() {
		return subjectClc;
	}

	public void setSubjectClc(String subjectClc) {
		this.subjectClc = subjectClc;
	}

	public String getReferences() {
		return references;
	}

	public void setReferences(String references) {
		this.references = references;
	}

	public List<String> getReprintAuthorList() {
		return reprintAuthorList;
	}

	public void setReprintAuthorList(List<String> reprintAuthorList) {
		this.reprintAuthorList = reprintAuthorList;
	}

	@Override
	public Document newInstance() {
		return new Dissertation();
	}
}
