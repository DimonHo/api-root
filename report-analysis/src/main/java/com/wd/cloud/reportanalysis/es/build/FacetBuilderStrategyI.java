package com.wd.cloud.reportanalysis.es.build;

import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;

public interface FacetBuilderStrategyI {

    public AbstractAggregationBuilder execute(String field);

}
