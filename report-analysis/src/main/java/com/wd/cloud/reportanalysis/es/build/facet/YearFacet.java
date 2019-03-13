package com.wd.cloud.reportanalysis.es.build.facet;

import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.stereotype.Component;

import com.wd.cloud.reportanalysis.es.build.FacetBuilderStrategyI;

import static org.elasticsearch.search.aggregations.AggregationBuilders.terms;


@Component("yearFacet")
public class YearFacet implements FacetBuilderStrategyI{
	
    @Override
    public AbstractAggregationBuilder execute(String field) {
        return terms("year").field("year").order(Terms.Order.term(false)).size(10).shardSize(20);
    }

}
