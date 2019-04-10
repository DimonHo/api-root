package com.wd.cloud.reportanalysis.util;

import com.wd.cloud.reportanalysis.es.build.FacetBuilderStrategyI;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class SearchRequestUtil {

    public static List<AbstractAggregationBuilder> buildFacetCondition(Map<String, String> facetComponentNames) {
        List<AbstractAggregationBuilder> aggregationList = new ArrayList<AbstractAggregationBuilder>();
        for (Entry<String, String> facetFlag : facetComponentNames.entrySet()) {
            FacetBuilderStrategyI facetStrategy = (FacetBuilderStrategyI) SpringContextUtil.getBean(facetFlag.getKey().trim() + "Facet");
            if (null == facetStrategy) {
                continue;
            }
            AbstractAggregationBuilder abstractAggregationBuilder = facetStrategy.execute(facetFlag.getValue());
            aggregationList.add(abstractAggregationBuilder);
        }
        return aggregationList;
    }

}
