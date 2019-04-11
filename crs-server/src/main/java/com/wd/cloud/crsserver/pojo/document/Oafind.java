package com.wd.cloud.crsserver.pojo.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.List;

/**
 * @Author: He Zhigang
 * @Date: 2019/4/11 8:49
 * @Description:
 */
@Data
@Document(indexName = "oafind_2.0", type = "periodical")
public class Oafind {

    String title;

    String viewTimes;

    String volume;

    Integer year;

    List<Integer> shoulu;

    String pmid;

    String pages;

    String number;

    String language;

    List<String> keywords;

    String issue;

    @Id
    String id;

    Integer hits;

    String fund;

    Integer esi;

    String email;

    Integer downloads;

    String downloadTimes;

    String doi;

    String documentType;

    String docType;

    List<Integer> codes;

    String classic;

    Integer cites;

    String authorsFull;

    List<String> authors;

    List<String> authorFacets;

    List<String> authorAnalyzed;

    List<String> affiliationAnalyzed;

    String abstractInfo;

    List<Affiliations> affiliations;

    Journal journal;

    ReferencesList referencesList;

    List<ReprintAuthor> reprintAuthor;

    ReprintAuthors reprintAuthors;

    Source source;

    @Data
    static class Affiliations{
//        public Affiliations(){}
//        public Affiliations(String affilication,String authors){
//            this.affilication = affilication;
//            this.authors = authors;
//        }
//        public Affiliations(String affilication,String authors,String email){
//            this.affilication = affilication;
//            this.authors = authors;
//            this.email = email;
//        }
        String affilication;
        String authors;
        String email;
    }

    @Data
    static class Journal{
        String eissn;
        String issn;
        String name;
        String nameAbb;
        String nameFacet;
    }

    @Data
    static class ReferencesList{
        String id;
        String reference;
    }

    @Data
    static class ReprintAuthor{
        String affilication;
        String authors;
        String email;
    }

    @Data
    static class ReprintAuthors{
        String affilication;
        String authors;
        String email;
    }

    @Data
    static class Source{
        String format;

        Boolean open;

        String name;

        Integer pubType;

        String url;

        Integer usable;
    }
}
