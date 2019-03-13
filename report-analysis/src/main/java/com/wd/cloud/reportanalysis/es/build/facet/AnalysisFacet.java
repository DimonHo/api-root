package com.wd.cloud.reportanalysis.es.build.facet;

import static org.elasticsearch.search.aggregations.AggregationBuilders.terms;

import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.springframework.stereotype.Component;

import com.wd.cloud.reportanalysis.es.build.FacetBuilderStrategyI;

@Component("analysisFacet")
public class AnalysisFacet implements FacetBuilderStrategyI{

	@Override
    public AbstractAggregationBuilder execute(String field) {
		if ("wosCitesAll".equals(field) || "wosCites".equals(field)) {        //总被引频次
		    TermsAggregationBuilder termsBuilders = AggregationBuilders.terms(field).field("year").size(Integer.MAX_VALUE).order(Terms.Order.term(true));
		    AggregationBuilder termsBuilder = AggregationBuilders.sum("wosCites").field("wosCites");
		    return termsBuilders.subAggregation(termsBuilder);
		}
		return terms(field).field(field).order(Terms.Order.term(true)).size(Integer.MAX_VALUE).shardSize(20).order(Terms.Order.term(true));
	}
}
