package com.wd.cloud.bse.data;

import com.hnlat.esmapping.annotation.All;
import com.hnlat.esmapping.annotation.ID;
import com.hnlat.esmapping.annotation.Property;
import com.hnlat.esmapping.annotation.Property.FieldType;
import com.hnlat.esmapping.annotation.Property.IndexType;
import com.hnlat.esmapping.annotation.Type;
import com.wd.cloud.bse.vo.LatConstant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Type(_all = @All(enabled = true, analyzer = "mmseg"))
public class ResourceIndex<T extends Document> {

    @ID
    private String id;

    @Property(type = FieldType.KEYEORD, index = IndexType.NOT_ANALYZED)
    private List<Integer> docType = new ArrayList<Integer>();

    @Property(type = FieldType.INTEGER, index = IndexType.NOT_ANALYZED)
    private Integer year;

    @Property(type = FieldType.KEYEORD, index = IndexType.NOT_ANALYZED)
    private List<String> shoulu = new ArrayList<String>();

    /**
     * ESI高水平论文期数;格式201705
     */
    @Property(type = FieldType.KEYEORD, index = IndexType.NOT_ANALYZED)
    private List<String> esiIssue;

    /**
     * 语言
     */
    @Property(type = FieldType.KEYEORD, index = IndexType.NOT_ANALYZED)
    private Integer docLan;

    /**
     * 期刊
     */
    @Property(type = FieldType.KEYEORD, index = IndexType.NOT_ANALYZED)
    private String jourTitleFacet;

    @Property(type = FieldType.KEYEORD, index = IndexType.NOT_ANALYZED)
    private String docTitleFacet;

    /**
     * ESI学科类别
     */
    @Property(type = FieldType.KEYEORD, index = IndexType.NOT_ANALYZED)
    private List<String> subject;

    /**
     * wos学科类别
     */
    @Property(type = FieldType.KEYEORD, index = IndexType.NOT_ANALYZED)
    private List<String> subjects;

    /**
     * 关联学科
     */
    @Property(type = FieldType.KEYEORD, index = IndexType.NOT_ANALYZED)
    private List<String> relationSubject;

    /**
     * 关键词(用于聚合查询)
     */
    @Property(type = FieldType.KEYEORD, index = IndexType.NOT_ANALYZED)
    private List<String> keywordList;

    /**
     * 作者(用于聚合查询)
     */
    @Property(type = FieldType.KEYEORD, index = IndexType.NOT_ANALYZED)
    private List<String> authorList;

    /**机构签名*/
//	@Property(index = IndexType.NOT_ANALYZED)
//	private List<Integer> orgSign = new ArrayList<Integer>();
//	
//	/**院系*/
//	@Property(index = IndexType.NOT_ANALYZED)
//	private List<Integer> colleges = new ArrayList<Integer>();

    /**
     * 导师(用于聚合查询)
     */
    @Property(type = FieldType.KEYEORD, index = IndexType.NOT_ANALYZED)
    private List<String> guiderList;

    @Property(type = FieldType.KEYEORD, index = IndexType.NOT_ANALYZED)
    private String degree;//学位

    /**
     * 学位授予单位
     */
    @Property(type = FieldType.KEYEORD, index = IndexType.NOT_ANALYZED)
    private String degreeAwarder;

    /**
     * 学科专业
     */
    @Property(type = FieldType.KEYEORD, index = IndexType.NOT_ANALYZED)
    private List<String> discipline;

    /**
     * 期刊会议：研究方向
     */
    @Property(type = FieldType.KEYEORD, index = IndexType.NOT_ANALYZED)
    private List<String> researchField;

    /**
     * wos被引频次
     */
    @Property(type = FieldType.INTEGER, index = IndexType.NOT_ANALYZED)
    private Integer wosCites = 0;

    @Property(type = FieldType.NESTED)
    private List<T> documents = new ArrayList<T>();

    @Property(type = FieldType.KEYEORD, index = IndexType.NOT_ANALYZED)
    private List<Integer> scids;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<T> getDocuments() {
        return documents;
    }

    public void setDocuments(List<T> documents) {
        this.documents = documents;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

//	public List<String> getShoulu() {
//		return shoulu;
//	}

    public Integer getDocLan() {
        return docLan;
    }

    public void setDocLan(Integer docLan) {
        this.docLan = docLan;
    }

    public String getJourTitleFacet() {
        return jourTitleFacet;
    }

    public void setJourTitleFacet(String jourTitleFacet) {
        this.jourTitleFacet = jourTitleFacet;
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

    public List<String> getGuiderList() {
        return guiderList;
    }

//	public List<Integer> getOrgSign() {
//		return orgSign;
//	}
//
//	public void setOrgSign(List<Integer> orgSign) {
//		this.orgSign = orgSign;
//	}
//
//	public List<Integer> getColleges() {
//		return colleges;
//	}
//
//	public void setColleges(List<Integer> colleges) {
//		this.colleges = colleges;
//	}

    public void setGuiderList(List<String> guiderList) {
        this.guiderList = guiderList;
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

    public List<String> getSubject() {
        return subject;
    }

    public void setSubject(List<String> subject) {
        this.subject = subject;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    public List<String> getEsiIssue() {
        return esiIssue;
    }

    public void setEsiIssue(List<String> esiIssue) {
        this.esiIssue = esiIssue;
    }

    public List<Integer> getDocType() {
        return docType;
    }

    public void setDocType(List<Integer> docType) {
        this.docType = docType;
    }

    public List<Integer> getScids() {
        return scids;
    }

    public void setScids(List<Integer> scids) {
        this.scids = scids;
    }

    public List<String> getDiscipline() {
        return discipline;
    }

    public void setDiscipline(List<String> discipline) {
        this.discipline = discipline;
    }

    public List<String> getResearchField() {
        return researchField;
    }

    public void setResearchField(List<String> researchField) {
        this.researchField = researchField;
    }

    public Integer getWosCites() {
        return wosCites;
    }

    public void setWosCites(Integer wosCites) {
        this.wosCites = wosCites;
    }

    public String getDocTitleFacet() {
        return docTitleFacet;
    }

    public void setDocTitleFacet(String docTitleFacet) {
        this.docTitleFacet = docTitleFacet;
    }

    public List<String> getRelationSubject() {
        return relationSubject;
    }

    public void setRelationSubject(List<String> relationSubject) {
        this.relationSubject = relationSubject;
    }

    public List<String> getShoulu() {        //2018-03-20：问题10362收录体系的展示顺序有误
        shoulu = findSame();
        shoulu.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int index1 = 1, index2 = 2;
                for (int i = 0; i < LatConstant.SHOULUS.length; i++) {
                    String sl = LatConstant.SHOULUS[i];
                    if (sl.equals(o1)) {
                        index1 = i;
                    }
                    if (sl.equals(o2)) {
                        index2 = i;
                    }
                }
                return index1 - index2;
            }

        });
        return shoulu;
    }

    public void setShoulu(List<String> shoulu) {
        this.shoulu = shoulu;
    }

    /**
     * 2018-03-20：问题10362收录体系的展示顺序有误
     * （未剔除不正确的收录）
     *
     * @return
     */
    private List<String> findSame() {
        List<String> new_shoulu = new ArrayList<String>();
        for (String soulu : shoulu) {        //剔除不正确的收录
            if ("MEDLINE".equals(soulu)) {
                soulu = "Medline";
            }
            if ("A&amp;HCI".equals(soulu)) {
                soulu = "A&HCI";
            }
            if ("CPCI".equals(soulu.toUpperCase())) {
                soulu = "CPCI-S";
            }
            if ("AHCI".equals(soulu.toUpperCase())) {
                soulu = "A&HCI";
            }
            List<String> SHOULUS = Arrays.asList(LatConstant.SHOULUS);
            if (SHOULUS.contains(soulu)) {
                new_shoulu.add(soulu);
            }
        }
        return new_shoulu;
    }

}
