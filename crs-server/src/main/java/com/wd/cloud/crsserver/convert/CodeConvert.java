package com.wd.cloud.crsserver.convert;

import com.wd.cloud.crsserver.pojo.entity.WdSubject;
import com.wd.cloud.crsserver.repository.WdSubjectRepository;
import com.weidu.commons.search.AggsResult;
import com.weidu.commons.search.AggsResult.Entry;
import com.weidu.commons.search.convert.AggsTermsConvert;
import lombok.Data;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shenfu
 * 学科编码字段转换
 */
@Component
public class CodeConvert extends AggsTermsConvert {

    @Autowired
    private WdSubjectRepository wdSubjectRepository;

    @Data
    public static class EntryGroup extends Entry {

        public EntryGroup(String groupName) {
            this.setTerm(groupName);
        }

        private List<Entry> subEntries = new ArrayList<Entry>();

        public void addSubEntry(Entry entry) {
            subEntries.add(entry);
        }

    }

    @Override
    public AggsResult convert(Aggregation aggregation) {
        Terms terms = (Terms) aggregation;
        AggsResult result = new AggsResult();

        List<WdSubject> subjects = wdSubjectRepository.findByIndexType("wos");
        EntryGroup wosGroup = new EntryGroup("SCI-E、SSCI、A&HCI");
        for (Terms.Bucket e : terms.getBuckets()) {
            for (WdSubject subject : subjects) {
                if ((subject.getId() + "").equals(e.getKeyAsString())) {
                    wosGroup.addSubEntry(convertTeam(e, subject));
                }
            }
        }
        result.addEntry(wosGroup);

        List<WdSubject> eiSubjects = wdSubjectRepository.findByIndexType("ei");
        EntryGroup eiGroup = new EntryGroup("EI");
        for (Terms.Bucket e : terms.getBuckets()) {
            for (WdSubject subject : eiSubjects) {
                if ((subject.getId() + "").equals(e.getKeyAsString())) {
                    eiGroup.addSubEntry(convertTeam(e, subject));
                }
            }
        }
        result.addEntry(eiGroup);

        List<WdSubject> esiSubjects = wdSubjectRepository.findByIndexType("esi");
        EntryGroup eisGroup = new EntryGroup("ESI");
        for (Terms.Bucket e : terms.getBuckets()) {
            for (WdSubject subject : esiSubjects) {
                if ((subject.getId() + "").equals(e.getKeyAsString())) {
                    eisGroup.addSubEntry(convertTeam(e, subject));
                }
            }
        }
        result.addEntry(eisGroup);

        List<WdSubject> niSubjects = wdSubjectRepository.findByIndexType("nature");
        EntryGroup niGroup = new EntryGroup("Nature Index");
        for (Terms.Bucket e : terms.getBuckets()) {
            for (WdSubject subject : niSubjects) {
                if ((subject.getId() + "").equals(e.getKeyAsString())) {
                    niGroup.addSubEntry(convertTeam(e, subject));
                }
            }
        }
        result.addEntry(niGroup);

        return result;
    }

    public Entry convertTeam(Terms.Bucket bucket, WdSubject subject) {
        return new Entry(subject.getNameEn() + " [" + subject.getNameCn() + "]", bucket.getDocCount(), bucket.getKeyAsString());
    }

    @Override
    public Entry convertTeam(Terms.Bucket bucket) {
        return new Entry(bucket.getKeyAsString(), bucket.getDocCount());
    }

}
