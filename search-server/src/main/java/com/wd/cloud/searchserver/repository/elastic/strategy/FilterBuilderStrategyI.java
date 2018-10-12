package com.wd.cloud.searchserver.repository.elastic.strategy;

import org.elasticsearch.index.query.BoolQueryBuilder;

import java.util.Set;


public interface FilterBuilderStrategyI {

    public BoolQueryBuilder execute(BoolQueryBuilder boolFilterBuilder, Set<String> valueSet);
}
