package com.wd.cloud.reportanalysis.es.build;

import com.wd.cloud.reportanalysis.entity.QueryCondition;
import org.elasticsearch.index.query.QueryBuilder;

public interface QueryBuilderStrategyI {

    public QueryBuilder execute(QueryCondition queryCondition);
}
