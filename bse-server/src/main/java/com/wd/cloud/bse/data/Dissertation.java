package com.wd.cloud.bse.data;


import com.hnlat.esmapping.annotation.Property;
import com.hnlat.esmapping.annotation.Property.FieldType;
import com.hnlat.esmapping.annotation.Property.IndexType;

/**
 * 学位论文
 *
 * @author Administrator
 */
public class Dissertation extends Document {

    /**
     * 导师
     */
    @Property(type = FieldType.TEXT, analyzer = "mmseg", boost = 4.9)
    private String guider;

    /**
     * 导师所属机构
     */
    @Property(type = FieldType.TEXT, analyzer = "mmseg", includeInAll = true, boost = 4.5)
    private String guiderOrg;

    @Property(index = IndexType.NOT_ANALYZED)
    private String doi;

    @Property(type = FieldType.KEYEORD, index = IndexType.NOT_ANALYZED)
    private String degree;//学位

    /**
     * 学位授予单位
     */
    @Property(type = FieldType.TEXT, analyzer = "mmseg", includeInAll = true, boost = 4.5)
    private String degreeAwarder;

    /**
     * 学科专业
     */
    @Property(type = FieldType.KEYEORD, index = IndexType.NOT_ANALYZED)
    private String discipline;
    private String researchFields;

    /**
     * 分类号
     */
    @Property(index = IndexType.NOT_ANALYZED)
    private String subjectClc;

    /**
     * 参考文献
     */
    @Property(type = FieldType.TEXT)
    private String references;


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

    public String getGuider() {
        return guider;
    }

    public void setGuider(String guider) {
        this.guider = guider;
    }

    public String getGuiderOrg() {
        return guiderOrg;
    }

    public void setGuiderOrg(String guiderOrg) {
        this.guiderOrg = guiderOrg;
    }

    public String getDiscipline() {
        return discipline;
    }

    public void setDiscipline(String discipline) {
        this.discipline = discipline;
    }

    public String getResearchFields() {
        return researchFields;
    }

    public void setResearchFields(String researchFields) {
        this.researchFields = researchFields;
    }


}
