package com.wd.cloud.bse.es.facet;


import com.google.common.collect.ImmutableMap;
import com.wd.cloud.bse.es.FacetConverter;
import com.wd.cloud.bse.vo.FacetResult;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component("docType")
public class DocTypeConverter implements FacetConverter {

    private static final ImmutableMap<Integer, String> map;

    static {
        map = ImmutableMap.<Integer, String>builder()
                .put(1, "期刊论文")
                .put(3, "会议论文")
//				.put(DocType.Patent.getKey(), "专利")
//				.put(DocType.Book.getKey(), "著作")
//				.put(DocType.Project.getKey(), "项目")
//				.put(DocType.Award.getKey(), "奖励")
                .put(2, "学位论文")
                .build();
    }

    public FacetResult.Entry convertTeam(Bucket entry) {
        String type = map.get(Integer.valueOf(entry.getKeyAsString()));
        if (type == null) {
            type = "其他";
        }
        return new FacetResult.Entry(type, entry.getDocCount(), entry.getKeyAsString());
    }

    @Override
    public FacetResult convert(Aggregation facet) {
        List<FacetResult.Entry> entries = new ArrayList<FacetResult.Entry>();
        FacetResult result = new FacetResult();
        Terms terms = (Terms) facet;
        Collection<Bucket> bucketColl = (Collection<Bucket>) terms.getBuckets();
        for (Bucket e : bucketColl) {
            FacetResult.Entry entry = convertTeam(e);
            entries.add(entry);
            result.addEntry(entry);
        }
        for (Map.Entry<Integer, String> sk : map.entrySet()) {
            if (!checkContain(entries, sk.getKey())) {
                result.addEntry(sk.getValue().toString(), 0);
            }
        }
        return result;
    }

    private boolean checkContain(List<FacetResult.Entry> entries, Integer sk) {
        boolean isContain = false;
        for (FacetResult.Entry entry : entries) {
            if (!sk.toString().equals(entry.getTerm())) {
                isContain = true;
            }
        }
        return isContain;
    }

}
