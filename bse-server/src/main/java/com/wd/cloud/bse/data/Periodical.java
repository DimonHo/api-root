package com.wd.cloud.bse.data;

import com.hnlat.esmapping.annotation.Property;
import com.hnlat.esmapping.annotation.Property.FieldType;
import com.hnlat.esmapping.annotation.Property.IndexType;
import com.wd.cloud.bse.service.transfrom.convert.AccessionNumConvert;
import com.wd.cloud.bse.vo.LatConstant;

import java.util.*;

public class Periodical extends Document {

    /**
     * 期刊
     */
    @Property(type = FieldType.TEXT, analyzer = "mmseg", includeInAll = true, boost = 8)
    private String journalTitle;

    /**
     * ISSN
     */
    @Property(index = IndexType.NOT_ANALYZED)
    private String issn;

    /**
     * 卷
     */
    @Property(index = IndexType.NO)
    private String volume;

    /**
     * 期
     */
    @Property(index = IndexType.NO)
    private String number;

    /**
     * 页码
     */
    @Property(index = IndexType.NO)
    private String pageRange;

    /**
     * 通讯作者
     */
    @Property(type = FieldType.TEXT, analyzer = "mmseg", boost = 4.9)
    private String reprintAuthor;

    /**
     * 通讯作者机构
     */
    @Property(type = FieldType.TEXT, analyzer = "mmseg", boost = 4.9)
    private String reprintAuthorOrg;

    /**
     * 学科
     */
    @Property(type = FieldType.TEXT, analyzer = "semicolon_spliter", includeInAll = true, boost = 4.5)
    private String subjects;

    /**
     * 期刊会议：研究方向
     */
    @Property(type = FieldType.TEXT, analyzer = "semicolon_spliter", includeInAll = true, boost = 4.5)
    private String researchField;

    /**
     * wos被引频次
     */
    @Property(index = IndexType.NOT_ANALYZED)
    private Integer wosCites = 0;

    /**
     * 邮箱
     */
    @Property(index = IndexType.NOT_ANALYZED)
    private String email;

    /**
     * 文献类别
     */
    @Property(type = FieldType.KEYEORD, index = IndexType.NOT_ANALYZED)
    private String category;

    /**
     * 入藏号
     */
    @Property(analyzer = "semicolon_spliter", includeInAll = true)
    private String accessionNum;

    @Property(index = IndexType.NOT_ANALYZED)
    private String doi;

    /**
     * 基金类别
     */
    @Property(index = IndexType.NO)
    private String fund;

    /**
     * 参考文献
     */
    @Property(type = FieldType.TEXT)
    private String references;

    @Property(type = FieldType.TEXT, index = IndexType.NOT_ANALYZED)
    private List<String> shoulu = new ArrayList<String>();

    public String getJournalTitle() {
        return journalTitle;
    }

    public void setJournalTitle(String journalTitle) {
        this.journalTitle = journalTitle;
    }

    public String getIssn() {
        return issn;
    }

    public void setIssn(String issn) {
        this.issn = issn;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPageRange() {
        return pageRange;
    }

    public void setPageRange(String pageRange) {
        this.pageRange = pageRange;
    }

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

    public String getSubjects() {
        return subjects;
    }

//	public String getSubjects() {
//		if(StringUtils.isNoneEmpty(subjects)) {
//			subjects = "ESI学科类别:" + subjects;
//		}
//		if(StringUtils.isNoneEmpty(subjects) && StringUtils.isNoneEmpty(researchField)) {
//			subjects = subjects + ";";
//		}
//		if(StringUtils.isNoneEmpty(researchField)) {
//			subjects = subjects + "WOS学科类别:" + researchField;
//		}
//		return subjects;
//	}

    public void setSubjects(String subjects) {
        this.subjects = subjects;
    }

    public String getResearchField() {
        return researchField;
    }

    public void setResearchField(String researchField) {
        this.researchField = researchField;
    }

    public Integer getWosCites() {
        return wosCites;
    }

    public void setWosCites(Integer wosCites) {
        this.wosCites = wosCites;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAccessionNum() {
        return accessionNum;
    }

    public void setAccessionNum(String accessionNum) {
        this.accessionNum = accessionNum;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getFund() {
        return fund;
    }

    public void setFund(String fund) {
        this.fund = fund;
    }

    public String getReferences() {
        return references;
    }

    public void setReferences(String references) {
        this.references = references;
    }

//	public List<String> getShoulu() {
//		return shoulu;
//	}

    public void setShoulu(List<String> shoulu) {
        this.shoulu = shoulu;
    }

    public void addShulu(Set<String> set) {
        set.addAll(shoulu);
        shoulu = new ArrayList<String>(set);
    }

    public Map<String, String> getShoulu_ex() {
        return AccessionNumConvert.convert(getAccessionNum());
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
