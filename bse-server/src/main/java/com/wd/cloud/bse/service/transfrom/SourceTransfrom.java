package com.wd.cloud.bse.service.transfrom;

import cn.hutool.core.util.StrUtil;
import com.wd.cloud.bse.data.Dissertation;
import com.wd.cloud.bse.data.Document;
import com.wd.cloud.bse.data.Periodical;
import com.wd.cloud.bse.data.Proceedings;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Component
public class SourceTransfrom {

    /**
     * .GB/T7714
     *
     * @return
     */
    public String getGB(Document doc, String authors) {
        String corpus = "", category = "", journalTitle = "", publisher = "", publisherCity = "", issue = "", pages = "", volume = "", year = "", GB = "", degreeAwarder = "";
        if (authors != null) {
            List<String> list = Arrays.asList(authors.split(";"));
            List<String> authorList = new ArrayList(list);
            if (authorList.size() > 3) {
                for (int i = authorList.size(); i > 3; i--) {
                    authorList.remove(i - 1);
                }
                authorList.add("et al");
            }
            String[] toBeStored = authorList.toArray(new String[authorList.size()]);
            String author = String.join(";", toBeStored);
            author = author.replaceAll(",", " ").replaceAll(";", ",");
            GB += author + ".";
        }
        if (doc.getYear() != null) {
            year = doc.getYear() + "";
        }
        Integer docLan = doc.getDocLan();
        String title = doc.getDocTitle();
        if (StrUtil.isNotBlank(title) && title.contains("#&#&#")) {
            if (docLan == null || docLan != 2) {
                title = title.split("#&#&#")[0];
            } else {
                title = title.split("#&#&#")[1];
            }
        }
        if (doc instanceof Dissertation) {
            Dissertation diss = (Dissertation) doc;
            degreeAwarder = diss.getDegreeAwarder();
        } else if (doc instanceof Periodical) {
            Periodical periodical = (Periodical) doc;
            volume = periodical.getVolume();
            pages = periodical.getPageRange();
            issue = periodical.getNumber();
            journalTitle = periodical.getJournalTitle();
            category = periodical.getCategory();
            if (StrUtil.isNotBlank(journalTitle) && journalTitle.contains("#&#&#")) {
                if (docLan == null || docLan != 2) {
                    journalTitle = journalTitle.split("#&#&#")[0];
                } else {
                    journalTitle = journalTitle.split("#&#&#")[1];
                }
            }
        } else if (doc instanceof Proceedings) {
            Proceedings proceedings = (Proceedings) doc;
            publisherCity = proceedings.getPubAddr();
            publisher = proceedings.getPublisher();
            corpus = proceedings.getCorpus();
        }
        List<Integer> docType = doc.getDocTypes();
        if (docType.contains(3)) {
            if (StrUtil.isNotBlank(corpus) && "proceedings paper".equalsIgnoreCase(category)) {//会议论文格式著录
                if (StrUtil.isNotBlank(title)) {
                    GB = GB + "" + title + "[M]//";
                }
                if (StrUtil.isNotBlank(journalTitle)) {
                    GB = GB + journalTitle + ".";
                }
                if (StrUtil.isNotBlank(publisher)) {
                    GB = GB + publisher + ",";
                }
                if (StrUtil.isNotBlank(year)) {
                    GB = GB + year + ":";
                }
                if (StrUtil.isNotBlank(pages)) {
                    GB = GB + pages + ".";
                } else {
                    GB = GB.substring(0, GB.length() - 1) + ".";
                }
            } else if ("BOOK".equalsIgnoreCase(category)
                    || "BOOK CHAPTER".equalsIgnoreCase(category)) { //专著格式著录
                if (StrUtil.isNotBlank(title)) {
                    GB = GB + "" + title + "[C]//";
                }
                if (StrUtil.isNotBlank(journalTitle)) {
                    GB = GB + journalTitle + ".";
                }
                if (StrUtil.isNotBlank(publisher)) {
                    GB = GB + publisher + ",";
                }
                if (StrUtil.isNotBlank(year)) {
                    GB = GB + year + ":";
                }
                if (StrUtil.isNotBlank(pages)) {
                    GB = GB + pages + ".";
                } else {
                    GB = GB.substring(0, GB.length() - 1) + ".";
                }
            } else {
                if (StrUtil.isNotBlank(title)) {
                    GB = GB + "" + title + "[J].";
                }
                if (StrUtil.isNotBlank(journalTitle)) {
                    GB = GB + journalTitle + ",";
                }
                if (StrUtil.isNotBlank(year)) {
                    GB = GB + year + ",";
                }
                if (StrUtil.isNotBlank(volume)) {//卷
                    GB = GB + volume;
                }
                if (StrUtil.isNotBlank(issue)) {
                    GB = GB + "(" + issue + "):";
                }
                if (StrUtil.isNotBlank(pages)) {
                    GB = GB + pages + ".";
                } else {
                    GB = GB.substring(0, GB.length() - 1) + ".";
                }
            }
        } else if (docType.contains(1)) {            //期刊
            if (StrUtil.isNotBlank(title)) {
                GB = GB + "" + title + "[J].";
            }
            if (StrUtil.isNotBlank(journalTitle)) {
                GB = GB + journalTitle + ",";
            }
            if (StrUtil.isNotBlank(year)) {
                GB = GB + year + ",";
            }
            if (StrUtil.isNotBlank(volume)) {//卷
                GB = GB + volume;
            }
            if (StrUtil.isNotBlank(issue)) {
                GB = GB + "(" + issue + "):";
            }
            if (StrUtil.isNotBlank(pages)) {
                GB = GB + pages + ".";
            } else {
                GB = GB.substring(0, GB.length() - 1) + ".";
            }
        } else if (docType.contains(2)) {        //学位
            if (StrUtil.isNotBlank(title)) {
                GB = GB + "" + title + "[D]";
            }
            if (StrUtil.isNotBlank(degreeAwarder)) {
                GB = GB + "" + degreeAwarder + ",";
            }
            if (StrUtil.isNotBlank(year)) {
                GB = GB + year + ".";
            }
        }
        return GB;
    }

    /**
     * .APA
     *
     * @return
     */
    public String getAPA(Document doc, String authors) {
        String corpus = "", category = "", journalTitle = "", publisher = "", publisherCity = "", issue = "", pages = "", volume = "", APA = "", degree = "", degreeAwarder = "";
        if (authors != null) {
//			List<String> authorList = Arrays.asList(authors.split(";"));
            List<String> list = Arrays.asList(authors.split(";"));
            List<String> authorList = new ArrayList(list);
            if (authorList.size() > 8) {
                String lastAuthor = authorList.get(authorList.size() - 1);
                for (int i = authorList.size(); i > 6; i--) {
                    authorList.remove(i - 1);
                }
                authorList.add("...");
                authorList.add(lastAuthor);
            }
            String[] toBeStored = authorList.toArray(new String[authorList.size()]);
            String author = String.join(";", toBeStored);
            author = author.replaceAll(",", " ").replaceAll(";", ",");
            APA += author.replace(",...,", "...") + ".";
        }
        if (doc.getYear() != null) {
            String year = doc.getYear() + "";
            APA += "(" + year + ").";
        }
        Integer docLan = doc.getDocLan();
        String title = doc.getDocTitle();
        if (StrUtil.isNotBlank(title) && title.contains("#&#&#")) {
            if (docLan == null || docLan != 2) {
                title = title.split("#&#&#")[0];
            } else {
                title = title.split("#&#&#")[1];
            }
        }

        if (doc instanceof Dissertation) {
            Dissertation diss = (Dissertation) doc;
            degree = diss.getDegree();
            degreeAwarder = diss.getDegreeAwarder();
        } else if (doc instanceof Periodical) {
            Periodical periodical = (Periodical) doc;
            volume = periodical.getVolume();
            pages = periodical.getPageRange();
            issue = periodical.getNumber();
            journalTitle = periodical.getJournalTitle();
            category = periodical.getCategory();
            if (StrUtil.isNotBlank(journalTitle) && journalTitle.contains("#&#&#")) {
                if (docLan == null || docLan != 2) {
                    journalTitle = journalTitle.split("#&#&#")[0];
                } else {
                    journalTitle = journalTitle.split("#&#&#")[1];
                }
            }
        } else if (doc instanceof Proceedings) {
            Proceedings proceedings = (Proceedings) doc;
            publisherCity = proceedings.getPubAddr();
            publisher = proceedings.getPublisher();
            corpus = proceedings.getCorpus();
        }

        List<Integer> docType = doc.getDocTypes();
        if (docType.contains(3)) {            //会议论文格式著录
            if (StrUtil.isNotBlank(corpus) && "proceedings paper".equalsIgnoreCase(category)) {//会议论文格式著录
                if (StrUtil.isNotBlank(journalTitle)) {
                    APA = APA + "<i>" + journalTitle + "</i>.";
                }
                if (StrUtil.isNotBlank(publisherCity)) {
                    APA = APA + publisherCity + ":";
                }
                if (StrUtil.isNotBlank(publisher)) {
                    APA = APA + publisher + ".";
                }
            } else if ("BOOK".equalsIgnoreCase(category)
                    || "BOOK CHAPTER".equalsIgnoreCase(category)) { //专著格式著录
                if (StrUtil.isNotBlank(journalTitle)) {
                    APA = APA + "<i>" + journalTitle + "</i>.";
                }
                if (StrUtil.isNotBlank(publisherCity)) {
                    APA = APA + publisherCity + ":";
                }
                if (StrUtil.isNotBlank(publisher)) {
                    APA = APA + publisher + ".";
                }
            } else {
                if (StrUtil.isNotBlank(title)) {
                    APA = APA + "" + title + ".";
                }
                if (StrUtil.isNotBlank(journalTitle)) {
                    APA = APA + "<i>" + journalTitle + "</i>,";
                }
                if (StrUtil.isNotBlank(volume)) {//卷
                    APA = APA + volume;
                }
                if (StrUtil.isNotBlank(issue)) {
                    APA = APA + "(" + issue + "),";
                }
                if (StrUtil.isNotBlank(pages)) {
                    APA = APA + pages + ".";
                } else {
                    APA = APA.substring(0, APA.length() - 1) + ".";
                }
                if (StrUtil.isNotBlank(publisher)) {
                    APA = APA + publisher + ".";
                }
            }
        } else if (docType.contains(1)) {            //期刊
            if (StrUtil.isNotBlank(title)) {
                APA = APA + "" + title + ".";
            }
            if (StrUtil.isNotBlank(journalTitle)) {
                APA = APA + "<i>" + journalTitle + "</i>,";
            }
            if (StrUtil.isNotBlank(volume)) {//卷
                APA = APA + volume;
            }
            if (StrUtil.isNotBlank(issue)) {
                APA = APA + "(" + issue + "),";
            }
            if (StrUtil.isNotBlank(pages)) {
                APA = APA + pages + ".";
            } else {
                APA = APA.substring(0, APA.length() - 1) + ".";
            }
            if (StrUtil.isNotBlank(publisher)) {
                APA = APA + publisher + ".";
            }
        } else if (docType.contains(2)) {        //学位
            if (StrUtil.isNotBlank(title)) {
                APA = APA + " <i>" + title + "<i>.";
            }
            if (StrUtil.isNotBlank(degree) || StrUtil.isNotBlank(degreeAwarder)) {
                APA = APA + "(";
            }
            if (StrUtil.isNotBlank(degree)) {
                if ("硕士".equals(degree)) {
                    degree = "Master dissertation";
                } else if ("博士".equals(degree)) {
                    degree = "Doctoral dissertation";
                }
                APA = APA + "" + degree;
            }
            if (StrUtil.isNotBlank(degree) && StrUtil.isNotBlank(degreeAwarder)) {
                APA = APA + ",";
            }
            if (StrUtil.isNotBlank(degreeAwarder)) {
                APA = APA + " " + degreeAwarder;
            }
            if (StrUtil.isNotBlank(degree) || StrUtil.isNotBlank(degreeAwarder)) {
                APA = APA + ").";
            }
        }
        return APA;
    }

    /**
     * .MLA
     *
     * @return
     */
    public String getMLA(Document doc, String authors) {
        String corpus = "", category = "", journalTitle = "", publisher = "", publisherCity = "", issue = "", pages = "", volume = "", year = "", MLA = "", degreeAwarder = "";
        if (authors != null) {
//			List<String> authorList = Arrays.asList(authors.split(";"));
            List<String> list = Arrays.asList(authors.split(";"));
            List<String> authorList = new ArrayList(list);
            if (authorList.size() >= 2) {
                int size = authorList.size() - 1;
                authorList.set(size, "and " + authorList.get(size));
            }
            String[] toBeStored = authorList.toArray(new String[authorList.size()]);
            String author = String.join(";", toBeStored);
            author = author.replaceAll(",", " ").replaceAll(";", ",");
            MLA += author.replace(",...,", "...") + ".";
        }
        if (doc.getYear() != null) {
            year = doc.getYear() + "";
        }
        Integer docLan = doc.getDocLan();
        String title = doc.getDocTitle();
        if (StrUtil.isNotBlank(title) && title.contains("#&#&#")) {
            if (docLan == null || docLan != 2) {
                title = title.split("#&#&#")[0];
            } else {
                title = title.split("#&#&#")[1];
            }
        }

        if (doc instanceof Dissertation) {
            Dissertation diss = (Dissertation) doc;
            degreeAwarder = diss.getDegreeAwarder();
        } else if (doc instanceof Periodical) {
            Periodical periodical = (Periodical) doc;
            volume = periodical.getVolume();
            pages = periodical.getPageRange();
            issue = periodical.getNumber();
            journalTitle = periodical.getJournalTitle();
            category = periodical.getCategory();
            if (StrUtil.isNotBlank(journalTitle) && journalTitle.contains("#&#&#")) {
                if (docLan == null || docLan != 2) {
                    journalTitle = journalTitle.split("#&#&#")[0];
                } else {
                    journalTitle = journalTitle.split("#&#&#")[1];
                }
            }
        } else if (doc instanceof Proceedings) {
            Proceedings proceedings = (Proceedings) doc;
            publisherCity = proceedings.getPubAddr();
            publisher = proceedings.getPublisher();
            corpus = proceedings.getCorpus();
        }
        List<Integer> docType = doc.getDocTypes();
        if (docType.contains(3)) {            //会议论文格式著录
            if (StrUtil.isNotBlank(corpus) && "proceedings paper".equalsIgnoreCase(category)) {//会议论文格式著录
                if (StrUtil.isNotBlank(title)) {
                    MLA = MLA + "\"" + title + ".\" ";
                }
                if (StrUtil.isNotBlank(journalTitle)) {
                    MLA = MLA + "<i>" + journalTitle + "</i>,"; //斜体
                }
                if (StrUtil.isNotBlank(year)) {
                    MLA = MLA + year + ":";
                }
                if (StrUtil.isNotBlank(pages)) {
                    MLA = MLA + pages + ".";
                } else {
                    MLA = MLA.substring(0, MLA.length() - 1) + ".";
                }
            } else if ("BOOK".equalsIgnoreCase(category)
                    || "BOOK CHAPTER".equalsIgnoreCase(category)) { //专著格式著录
                if (StrUtil.isNotBlank(journalTitle)) {
                    MLA = MLA + "<i>" + journalTitle + "</i>."; //斜体
                }
                if (StrUtil.isNotBlank(publisherCity)) {
                    MLA = MLA + publisherCity + ":";
                }
                if (StrUtil.isNotBlank(publisher)) {
                    MLA = MLA + publisher + ",";
                }
                if (StrUtil.isNotBlank(year)) {
                    MLA = MLA + "(" + year + ").";
                }
            } else {
                if (StrUtil.isNotBlank(title)) {
                    MLA = MLA + "\"" + title + ".\"";
                }
                if (StrUtil.isNotBlank(journalTitle)) {
                    MLA = MLA + "<i>" + journalTitle + "</i> "; //斜体
                }
                if (StrUtil.isNotBlank(publisher)) {
                    MLA = MLA + publisher + ",";
                }
                if (StrUtil.isNotBlank(volume)) {//卷
                    MLA = MLA + volume + ".";
                }
                if (StrUtil.isNotBlank(issue)) {//期
                    MLA = MLA + issue;
                }
                if (StrUtil.isNotBlank(year)) {
                    MLA = MLA + "(" + year + "):";
                }
                if (StrUtil.isNotBlank(pages)) {
                    MLA = MLA + pages + ".";
                } else {
                    MLA = MLA.substring(0, MLA.length() - 1) + ".";
                }
            }
        } else if (docType.contains(1)) {            //期刊
            if (StrUtil.isNotBlank(title)) {
                MLA = MLA + "\"" + title + ".\"";
            }
            if (StrUtil.isNotBlank(journalTitle)) {
                MLA = MLA + "<i>" + journalTitle + "</i> "; //斜体
            }
            if (StrUtil.isNotBlank(publisher)) {
                MLA = MLA + publisher + ",";
            }
            if (StrUtil.isNotBlank(volume)) {//卷
                MLA = MLA + volume + ".";
            }
            if (StrUtil.isNotBlank(issue)) {//期
                MLA = MLA + issue;
            }
            if (StrUtil.isNotBlank(year)) {
                MLA = MLA + "(" + year + "):";
            }
            if (StrUtil.isNotBlank(pages)) {
                MLA = MLA + pages + ".";
            } else {
                MLA = MLA.substring(0, MLA.length() - 1) + ".";
            }
        } else if (docType.contains(2)) {        //学位
            if (StrUtil.isNotBlank(title)) {
                MLA = MLA + "<i>" + title + "<i>.Diss.";
            }
            if (StrUtil.isNotBlank(degreeAwarder)) {
                MLA = MLA + "" + degreeAwarder + ",";
            }
            if (StrUtil.isNotBlank(year)) {
                MLA = MLA + year + ".";
            }
        }
        return MLA;
    }

}
