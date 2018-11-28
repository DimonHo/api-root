package org.bse.server.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class Periodical extends Document {

	/**期刊*/
	private String journalTitle;
	
	private String jourTitleFacet;
	
	/**ISSN*/
	private String issn;
	
	private String eissn;
	
	private String cssn;
	
	/**卷*/
	private String volume;
	
	/**期*/
	private String number;
	
	/**页码*/
	private String pageRange;
	
	/**通讯作者*/
	private String reprintAuthor;
	
	/**通讯作者机构*/
	//@Property(indexAnalyzer="ansj_normal", searchAnalyzer="ansj_search", boost=4.9)
	private String reprintAuthorOrg;
	
	/**第一作者机构*/
	//@Property(indexAnalyzer="ansj_normal", searchAnalyzer="ansj_search", boost=4.9)
	private String firstAuthorOrg;
	
	/**ESI高水平论文期数;格式201705*/
	private List<String> esiIssue;
	
	/**文献类别*/
	private String category;
	
	/**基金类别*/
	private String fund;
	
	/**参考文献*/
	private String references;
	
	/**入藏号*/
	private String accessionNum;
	
	private List<String> shoulu = new ArrayList<String>();
	
	private String doi;
	
	private Integer wosCites = 0;
	
	private String subject;
	
	private String subjects;

	/**邮箱*/
	private String emails;
	
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

	public String getEissn() {
		return eissn;
	}

	public void setEissn(String eissn) {
		this.eissn = eissn;
	}

	public String getCssn() {
		return cssn;
	}

	public void setCssn(String cssn) {
		this.cssn = cssn;
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
		return repeat(reprintAuthor, ";");
//		return reprintAuthor;
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

	public String getFirstAuthorOrg() {
		return firstAuthorOrg;
	}

	public void setFirstAuthorOrg(String firstAuthorOrg) {
		this.firstAuthorOrg = firstAuthorOrg;
	}

	public List<String> getEsiIssue() {
		return esiIssue;
	}

	public void setEsiIssue(List<String> esiIssue) {
		this.esiIssue = esiIssue;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
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

	public String getAccessionNum() {
		return accessionNum;
	}

	public void setAccessionNum(String accessionNum) {
		this.accessionNum = accessionNum;
	}

//	public List<String> getShoulu() {		//2018-03-20：问题10362收录体系的展示顺序有误
//		shoulu = findSame();
//		shoulu.sort(new Comparator<String>(){
//			@Override
//			public int compare(String o1, String o2) {
//				int index1= 1, index2 = 2;
//				for(int i =0; i<Constants.SHOULUS.length;i++){
//					String sl = Constants.SHOULUS[i];
//					if(sl.equals(o1)){
//						index1 = i;
//					}
//					if(sl.equals(o2)){
//						index2= i;
//					}
//				}
//				return index1-index2;
//			}
//			
//		});
//		return shoulu;
//	}

	public void setShoulu(List<String> shoulu) {
		this.shoulu = shoulu;
	}

	public String getDoi() {
		return doi;
	}

	public void setDoi(String doi) {
		this.doi = doi;
	}

	public Integer getWosCites() {
		return wosCites;
	}

	public void setWosCites(Integer wosCites) {
		this.wosCites = wosCites;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSubjects() {
		return subjects;
	}

	public void setSubjects(String subjects) {
		this.subjects = subjects;
	}

	public String getEmails() {
		return emails;
	}

	public void setEmails(String emails) {
		this.emails = emails;
	}

	public String getJourTitleFacet() {
		return jourTitleFacet;
	}

	public void setJourTitleFacet(String jourTitleFacet) {
		this.jourTitleFacet = jourTitleFacet;
	}

	@Override
	public Document newInstance() {
		return new Periodical();
	}
	
	public void addShulu(Set<String> set) {
		set.addAll(shoulu);
		shoulu = new ArrayList<String>(set);
	}
	
	/**
	 * 2018-03-20：问题10362收录体系的展示顺序有误
	 * （未剔除不正确的收录）
	 * @return
	 */
//	private List<String> findSame() {
//		List<String> new_shoulu = new ArrayList<String>();
//		for (String soulu : shoulu) {		//剔除不正确的收录
//			if(soulu.equals("MEDLINE")) soulu = "Medline";
//			if(soulu.equals("A&amp;HCI")) soulu = "A&HCI";
//			List<String> SHOULUS = Arrays.asList(Constants.SHOULUS);
////			if(SHOULUS.contains(soulu)) {
//				new_shoulu.add(soulu);
////			}
//		}
//		return new_shoulu;
//	}

}
