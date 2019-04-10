package com.wd.cloud.bse.es.facet;


import com.google.common.collect.ImmutableMap;
import com.wd.cloud.bse.vo.FacetResult.Entry;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.springframework.stereotype.Component;

@Component("docLan")
public class DocLanConverter extends AbstractFacetConverter {

    private static final ImmutableMap<Integer, String> map;

    static {
        map = ImmutableMap.<Integer, String>builder()
                .put(1, "中文")
                .put(2, "英文")
                .put(3, "其他")
//				.put(0, "其他")
                .build();
    }

    @Override
    public Entry convertTeam(Bucket entry) {
        String lan = map.get(Integer.valueOf(entry.getKeyAsString()));
//		if(lan == null){
//			lan = "其他";
//		}
        if (Integer.parseInt(entry.getKeyAsString()) == 3) {
            lan = "其他";
        }
        return new Entry(lan, entry.getDocCount(), entry.getKeyAsString());
    }

}
