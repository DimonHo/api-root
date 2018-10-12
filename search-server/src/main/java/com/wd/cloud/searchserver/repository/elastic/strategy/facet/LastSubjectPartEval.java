package com.wd.cloud.searchserver.repository.elastic.strategy.facet;


import com.wd.cloud.searchserver.repository.elastic.strategy.FacetBuilderStrategyI;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.springframework.stereotype.Component;

import static org.elasticsearch.search.aggregations.AggregationBuilders.terms;


@Component("lastSubjectPartEval")
public class LastSubjectPartEval implements FacetBuilderStrategyI {

    @Override
    public AbstractAggregationBuilder execute(String field) {
        return terms("lastSubjectPartEval").field("shouLu.last").size(10).shardSize(20);
    }

}
