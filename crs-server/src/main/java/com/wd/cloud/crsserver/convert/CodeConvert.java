package com.wd.cloud.crsserver.convert;

import com.wd.cloud.crsserver.pojo.entity.WdSubject;
import com.wd.cloud.crsserver.repository.WdSubjectRepository;
import com.weidu.commons.search.AggsResult;
import com.weidu.commons.search.AggsResult.Entry;
import com.weidu.commons.search.convert.AggsTermsConvert;
import lombok.Data;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
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
        AggsResult result = new AggsResult();
        List<WdSubject> wosSubjects = wdSubjectRepository.findByIndexType("wos");
        EntryGroup wosGroup = new EntryGroup("SCI-E、SSCI、A&HCI");
        if (aggregation instanceof Filter){
            ((Filter)aggregation).getAggregations().forEach(aggs -> group(wosSubjects, wosGroup, (Terms) aggs));
        }else if (aggregation instanceof Terms){
            group(wosSubjects, wosGroup, (Terms) aggregation);
        }
        result.addEntry(wosGroup);

        List<WdSubject> eiSubjects = wdSubjectRepository.findByIndexType("ei");
        EntryGroup eiGroup = new EntryGroup("EI");
        if (aggregation instanceof Filter){
            ((Filter)aggregation).getAggregations().forEach(aggs -> group(eiSubjects, eiGroup, (Terms) aggs));
        }else if (aggregation instanceof Terms){
            group(eiSubjects, eiGroup, (Terms) aggregation);
        }
        result.addEntry(eiGroup);

        List<WdSubject> esiSubjects = wdSubjectRepository.findByIndexType("esi");
        EntryGroup eisGroup = new EntryGroup("ESI");
        if (aggregation instanceof Filter){
            ((Filter)aggregation).getAggregations().forEach(aggs -> group(esiSubjects, eisGroup, (Terms) aggs));
        }else if (aggregation instanceof Terms){
            group(esiSubjects, eisGroup, (Terms) aggregation);
        }
        result.addEntry(eisGroup);

        List<WdSubject> niSubjects = wdSubjectRepository.findByIndexType("nature");
        EntryGroup niGroup = new EntryGroup("Nature Index");
        if (aggregation instanceof Filter){
            ((Filter)aggregation).getAggregations().forEach(aggs -> group(niSubjects, niGroup, (Terms) aggs));
        }else if (aggregation instanceof Terms){
            group(niSubjects, niGroup, (Terms) aggregation);
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

    private void group(List<WdSubject> subjects, EntryGroup group, Terms aggs) {
        aggs.getBuckets().forEach(subAggs -> subjects.forEach(subject -> {
            if ((subject.getId() + "").equals(subAggs.getKeyAsString())){
                group.addSubEntry(convertTeam(subAggs,subject));
            }
        }));
    }

}
