package com.wd.cloud.bse.service.transfrom;

import com.wd.cloud.bse.data.Dissertation;
import com.wd.cloud.bse.data.Document;
import com.wd.cloud.bse.data.Periodical;
import com.wd.cloud.bse.data.Proceedings;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Component
public class SourceTransfrom {
	
	/**
	 * .GB/T7714
	 * @return
	 */
	public String getGB(Document doc,String authors) {
		String corpus="",category="",journalTitle="",publisher="",publisherCity="",issue="",pages="",volume = "",year="",GB="",degreeAwarder="";
		if(authors != null) {
			List<String> list = Arrays.asList(authors.split(";"));
			List<String> authorList = new ArrayList(list);
			if(authorList.size()>3) {
				for(int i= authorList.size();i>3;i--) {
					authorList.remove(i-1);
				}
				authorList.add("et al");
			}
			String[] toBeStored = authorList.toArray(new String[authorList.size()]);
			String author = String.join(";", toBeStored);
			author = author.replaceAll(",", " ").replaceAll(";", ",");
			GB += author + ".";
		}
		if(doc.getYear() != null) {
			year = doc.getYear() + "";
		}
		Integer docLan = doc.getDocLan();
		String title = doc.getDocTitle();
		if(StringUtils.isNotEmpty(title) && title.contains("#&#&#")) {
			if(docLan == null || docLan != 2) {
				title = title.split("#&#&#")[0];
			} else {
				title = title.split("#&#&#")[1];
			}
		}
		if(doc instanceof Dissertation) {
			Dissertation diss = (Dissertation) doc;
			degreeAwarder = diss.getDegreeAwarder();
		} else if(doc instanceof Periodical) {
			Periodical periodical = (Periodical) doc;
			volume = periodical.getVolume();
			pages = periodical.getPageRange();
			issue = periodical.getNumber();
			journalTitle = periodical.getJournalTitle();
			category = periodical.getCategory();
			if(StringUtils.isNotEmpty(journalTitle) && journalTitle.contains("#&#&#")) {
				if(docLan == null || docLan != 2) {
					journalTitle = journalTitle.split("#&#&#")[0];
				} else {
					journalTitle = journalTitle.split("#&#&#")[1];
				}
			}
		} else if(doc instanceof Proceedings) {
			Proceedings proceedings = (Proceedings) doc;
			publisherCity = proceedings.getPubAddr();
			publisher = proceedings.getPublisher();
			corpus = proceedings.getCorpus();
		}
		List<Integer> docType = doc.getDocTypes();
		if(docType.contains(3)) {			
			if(StringUtils.isNotEmpty(corpus) && "proceedings paper".equalsIgnoreCase(category)) {//会议论文格式著录
				if(StringUtils.isNotEmpty(title)) {
					GB = GB + "" + title+ "[M]//";
				}
				if(StringUtils.isNotEmpty(journalTitle)) {
					GB = GB + journalTitle+ ".";
				}
				if(StringUtils.isNotEmpty(publisher)) {
					GB = GB + publisher+ ",";
				}
				if(StringUtils.isNotEmpty(year)) {
					GB = GB + year+ ":";
				}
				if(StringUtils.isNotEmpty(pages)) {
					GB = GB + pages+ ".";
				} else {
					GB = GB.substring(0, GB.length()-1) + ".";
				}
			} else if("BOOK".equalsIgnoreCase(category) 
					|| "BOOK CHAPTER".equalsIgnoreCase(category)) { //专著格式著录
				if(StringUtils.isNotEmpty(title)) {
					GB = GB + "" + title+ "[C]//";
				}
				if(StringUtils.isNotEmpty(journalTitle)) {
					GB = GB + journalTitle+ ".";
				}
				if(StringUtils.isNotEmpty(publisher)) {
					GB = GB + publisher+ ",";
				}
				if(StringUtils.isNotEmpty(year)) {
					GB = GB + year+ ":";
				}
				if(StringUtils.isNotEmpty(pages)) {
					GB = GB + pages+ ".";
				} else {
					GB = GB.substring(0, GB.length()-1) + ".";
				}
			} else {
				if(StringUtils.isNotEmpty(title)) {
					GB = GB + "" + title+ "[J].";
				}
				if(StringUtils.isNotEmpty(journalTitle)) {
					GB = GB + journalTitle+ ",";
				}
				if(StringUtils.isNotEmpty(year)) {
					GB = GB + year+ ",";
				}
				if(StringUtils.isNotEmpty(volume)) {//卷
					GB = GB + volume;
				}
				if(StringUtils.isNotEmpty(issue)) {
					GB = GB + "(" + issue + "):";
				}
				if(StringUtils.isNotEmpty(pages)) {
					GB = GB + pages+ ".";
				} else {
					GB = GB.substring(0, GB.length()-1) + ".";
				}
			}
		} else if(docType.contains(1)) {			//期刊
			if(StringUtils.isNotEmpty(title)) {
				GB = GB + "" + title+ "[J].";
			}
			if(StringUtils.isNotEmpty(journalTitle)) {
				GB = GB + journalTitle+ ",";
			}
			if(StringUtils.isNotEmpty(year)) {
				GB = GB + year+ ",";
			}
			if(StringUtils.isNotEmpty(volume)) {//卷
				GB = GB + volume;
			}
			if(StringUtils.isNotEmpty(issue)) {
				GB = GB + "(" + issue + "):";
			}
			if(StringUtils.isNotEmpty(pages)) {
				GB = GB + pages+ ".";
			} else {
				GB = GB.substring(0, GB.length()-1) + ".";
			}
		} else if(docType.contains(2)){		//学位
			if(StringUtils.isNotEmpty(title)) {
				GB = GB + "" + title+ "[D]";
			}
			if(StringUtils.isNotEmpty(degreeAwarder)) {
				GB = GB + "" + degreeAwarder+ ",";
			}
			if(StringUtils.isNotEmpty(year)) {
				GB = GB + year+ ".";
			}
		}
		return GB;
	}
	/**
	 * .APA
	 * @return
	 */
	public String getAPA(Document doc,String authors) {
		String corpus="",category="",journalTitle="",publisher="",publisherCity="",issue="",pages="",volume = "",APA="",degree="",degreeAwarder="";
		if(authors != null) {
//			List<String> authorList = Arrays.asList(authors.split(";"));
			List<String> list = Arrays.asList(authors.split(";"));
			List<String> authorList = new ArrayList(list);
			if(authorList.size()>8) {
				String lastAuthor = authorList.get(authorList.size()-1);
				for(int i= authorList.size();i>6;i--) {
					authorList.remove(i-1);
				}
				authorList.add("...");
				authorList.add(lastAuthor);
			}
			String[] toBeStored = authorList.toArray(new String[authorList.size()]);
			String author = String.join(";", toBeStored);
			author = author.replaceAll(",", " ").replaceAll(";", ",");
			APA += author.replace(",...,", "...") + ".";
		}
		if(doc.getYear() != null) {
			String year = doc.getYear() + "";
			APA += "("+year + ").";
		}
		Integer docLan = doc.getDocLan();
		String title = doc.getDocTitle();
		if(StringUtils.isNotEmpty(title) && title.contains("#&#&#")) {
			if(docLan == null || docLan != 2) {
				title = title.split("#&#&#")[0];
			} else {
				title = title.split("#&#&#")[1];
			}
		}
		
		if(doc instanceof Dissertation) {
			Dissertation diss = (Dissertation) doc;
			degree = diss.getDegree();
			degreeAwarder = diss.getDegreeAwarder();
		} else if(doc instanceof Periodical) {
			Periodical periodical = (Periodical) doc;
			volume = periodical.getVolume();
			pages = periodical.getPageRange();
			issue = periodical.getNumber();
			journalTitle = periodical.getJournalTitle();
			category = periodical.getCategory();
			if(StringUtils.isNotEmpty(journalTitle) && journalTitle.contains("#&#&#")) {
				if(docLan == null || docLan != 2) {
					journalTitle = journalTitle.split("#&#&#")[0];
				} else {
					journalTitle = journalTitle.split("#&#&#")[1];
				}
			}
		} else if(doc instanceof Proceedings) {
			Proceedings proceedings = (Proceedings) doc;
			publisherCity = proceedings.getPubAddr();
			publisher = proceedings.getPublisher();
			corpus = proceedings.getCorpus();
		}
		
		List<Integer> docType = doc.getDocTypes();
		if(docType.contains(3)) {			//会议论文格式著录
			if(StringUtils.isNotEmpty(corpus) && "proceedings paper".equalsIgnoreCase(category)) {//会议论文格式著录
				if(StringUtils.isNotEmpty(journalTitle)) {
					APA = APA + "<i>"+journalTitle+ "</i>.";
				}
				if(StringUtils.isNotEmpty(publisherCity)) {
					APA = APA + publisherCity+ ":";
				}
				if(StringUtils.isNotEmpty(publisher)) {
					APA = APA + publisher+ ".";
				}
			} else if("BOOK".equalsIgnoreCase(category) 
					|| "BOOK CHAPTER".equalsIgnoreCase(category)) { //专著格式著录
				if(StringUtils.isNotEmpty(journalTitle)) {
					APA = APA + "<i>"+journalTitle+ "</i>.";
				}
				if(StringUtils.isNotEmpty(publisherCity)) {
					APA = APA + publisherCity+ ":";
				}
				if(StringUtils.isNotEmpty(publisher)) {
					APA = APA + publisher+ ".";
				}
			} else {
				if(StringUtils.isNotEmpty(title)) {
					APA = APA + "" + title+ ".";
				}
				if(StringUtils.isNotEmpty(journalTitle)) {
					APA = APA + "<i>"+journalTitle+ "</i>,";
				}
				if(StringUtils.isNotEmpty(volume)) {//卷
					APA = APA + volume;
				}
				if(StringUtils.isNotEmpty(issue)) {
					APA = APA + "(" + issue + "),";
				}
				if(StringUtils.isNotEmpty(pages)) {
					APA = APA + pages+ ".";
				} else {
					APA = APA.substring(0, APA.length()-1) + ".";
				}
				if(StringUtils.isNotEmpty(publisher)) {
					APA = APA + publisher+ ".";
				}
			}
		} else if(docType.contains(1)) {			//期刊
			if(StringUtils.isNotEmpty(title)) {
				APA = APA + "" + title+ ".";
			}
			if(StringUtils.isNotEmpty(journalTitle)) {
				APA = APA + "<i>"+journalTitle+ "</i>,";
			}
			if(StringUtils.isNotEmpty(volume)) {//卷
				APA = APA + volume;
			}
			if(StringUtils.isNotEmpty(issue)) {
				APA = APA + "(" + issue + "),";
			}
			if(StringUtils.isNotEmpty(pages)) {
				APA = APA + pages+ ".";
			} else {
				APA = APA.substring(0, APA.length()-1) + ".";
			}
			if(StringUtils.isNotEmpty(publisher)) {
				APA = APA + publisher+ ".";
			}
		} else if(docType.contains(2)){		//学位
			if(StringUtils.isNotEmpty(title)) {
				APA = APA + " <i>" + title+ "<i>.";
			}
			if(StringUtils.isNotEmpty(degree) || StringUtils.isNotEmpty(degreeAwarder)) {
				APA = APA + "(";
			}
			if(StringUtils.isNotEmpty(degree)) {
				if("硕士".equals(degree)) {
					degree = "Master dissertation";
				} else if("博士".equals(degree)) {
					degree = "Doctoral dissertation";
				}
				APA = APA + "" + degree;
			}
			if(StringUtils.isNotEmpty(degree) && StringUtils.isNotEmpty(degreeAwarder)) {
				APA = APA + ",";
			}
			if(StringUtils.isNotEmpty(degreeAwarder)) {
				APA = APA + " " + degreeAwarder;
			}
			if(StringUtils.isNotEmpty(degree) || StringUtils.isNotEmpty(degreeAwarder)) {
				APA = APA + ").";
			}
		}
		return APA;
	}
	/**
	 * .MLA
	 * @return
	 */
	public String getMLA(Document doc,String authors) {
		String corpus="",category="",journalTitle="",publisher="",publisherCity="",issue="",pages="",volume = "",year="",MLA="",degreeAwarder="";
		if(authors != null) {
//			List<String> authorList = Arrays.asList(authors.split(";"));
			List<String> list = Arrays.asList(authors.split(";"));
			List<String> authorList = new ArrayList(list);
			if(authorList.size() >= 2) {
				int size = authorList.size()-1;
				authorList.set(size, "and "+authorList.get(size));
			}
			String[] toBeStored = authorList.toArray(new String[authorList.size()]);
			String author = String.join(";", toBeStored);
			author = author.replaceAll(",", " ").replaceAll(";", ",");
			MLA += author.replace(",...,", "...") + ".";
		}
		if(doc.getYear() != null) {
			year = doc.getYear() + "";
		}
		Integer docLan = doc.getDocLan();
		String title = doc.getDocTitle();
		if(StringUtils.isNotEmpty(title) && title.contains("#&#&#")) {
			if(docLan == null || docLan != 2) {
				title = title.split("#&#&#")[0];
			} else {
				title = title.split("#&#&#")[1];
			}
		}
		
		if(doc instanceof Dissertation) {
			Dissertation diss = (Dissertation) doc;
			degreeAwarder = diss.getDegreeAwarder();
		} else if(doc instanceof Periodical) {
			Periodical periodical = (Periodical) doc;
			volume = periodical.getVolume();
			pages = periodical.getPageRange();
			issue = periodical.getNumber();
			journalTitle = periodical.getJournalTitle();
			category = periodical.getCategory();
			if(StringUtils.isNotEmpty(journalTitle) && journalTitle.contains("#&#&#")) {
				if(docLan == null || docLan != 2) {
					journalTitle = journalTitle.split("#&#&#")[0];
				} else {
					journalTitle = journalTitle.split("#&#&#")[1];
				}
			}
		} else if(doc instanceof Proceedings) {
			Proceedings proceedings = (Proceedings) doc;
			publisherCity = proceedings.getPubAddr();
			publisher = proceedings.getPublisher();
			corpus = proceedings.getCorpus();
		}
		List<Integer> docType = doc.getDocTypes();
		if(docType.contains(3)) {			//会议论文格式著录
			if(StringUtils.isNotEmpty(corpus) && "proceedings paper".equalsIgnoreCase(category)) {//会议论文格式著录
				if(StringUtils.isNotEmpty(title)) {
					MLA = MLA + "\"" + title+ ".\" ";
				}
				if(StringUtils.isNotEmpty(journalTitle)) {
					MLA = MLA + "<i>"+journalTitle+ "</i>,"; //斜体
				}
				if(StringUtils.isNotEmpty(year)) {
					MLA = MLA + year+ ":";
				}
				if(StringUtils.isNotEmpty(pages)) {
					MLA = MLA + pages+ ".";
				} else {
					MLA = MLA.substring(0, MLA.length()-1) + ".";
				}
			} else if("BOOK".equalsIgnoreCase(category) 
					|| "BOOK CHAPTER".equalsIgnoreCase(category)) { //专著格式著录
				if(StringUtils.isNotEmpty(journalTitle)) {
					MLA = MLA + "<i>"+journalTitle+ "</i>."; //斜体
				}
				if(StringUtils.isNotEmpty(publisherCity)) {
					MLA = MLA + publisherCity+ ":";
				}
				if(StringUtils.isNotEmpty(publisher)) {
					MLA = MLA + publisher+ ",";
				}
				if(StringUtils.isNotEmpty(year)) {
					MLA = MLA +"("+ year+ ").";
				}
			} else {
				if(StringUtils.isNotEmpty(title)) {
					MLA = MLA + "\"" + title+ ".\"";
				}
				if(StringUtils.isNotEmpty(journalTitle)) {
					MLA = MLA + "<i>"+journalTitle+ "</i> "; //斜体
				}
				if(StringUtils.isNotEmpty(publisher)) {
					MLA = MLA + publisher+ ",";
				}
				if(StringUtils.isNotEmpty(volume)) {//卷
					MLA = MLA + volume + ".";
				}
				if(StringUtils.isNotEmpty(issue)) {//期
					MLA = MLA + issue;
				}
				if(StringUtils.isNotEmpty(year)) {
					MLA = MLA +"("+ year+ "):";
				}
				if(StringUtils.isNotEmpty(pages)) {
					MLA = MLA + pages+ ".";
				} else {
					MLA = MLA.substring(0, MLA.length()-1) + ".";
				}
			}
		} else if(docType.contains(1)) {			//期刊
			if(StringUtils.isNotEmpty(title)) {
				MLA = MLA + "\"" + title+ ".\"";
			}
			if(StringUtils.isNotEmpty(journalTitle)) {
				MLA = MLA + "<i>"+journalTitle+ "</i> "; //斜体
			}
			if(StringUtils.isNotEmpty(publisher)) {
				MLA = MLA + publisher+ ",";
			}
			if(StringUtils.isNotEmpty(volume)) {//卷
				MLA = MLA + volume + ".";
			}
			if(StringUtils.isNotEmpty(issue)) {//期
				MLA = MLA + issue;
			}
			if(StringUtils.isNotEmpty(year)) {
				MLA = MLA +"("+ year+ "):";
			}
			if(StringUtils.isNotEmpty(pages)) {
				MLA = MLA + pages+ ".";
			} else {
				MLA = MLA.substring(0, MLA.length()-1) + ".";
			}
		} else if(docType.contains(2)){		//学位
			if(StringUtils.isNotEmpty(title)) {
				MLA = MLA + "<i>" + title+ "<i>.Diss.";
			}
			if(StringUtils.isNotEmpty(degreeAwarder)) {
				MLA = MLA + "" + degreeAwarder+ ",";
			}
			if(StringUtils.isNotEmpty(year)) {
				MLA = MLA + year+ ".";
			}
		}
		return MLA;
	}

}
