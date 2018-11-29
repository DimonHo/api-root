package com.wd.cloud.bse.es;


import com.wd.cloud.bse.vo.QueryCondition;
import org.elasticsearch.index.query.QueryBuilder;


public interface IFilterBuilderStrategy {

	public QueryBuilder build(QueryCondition queryCondition);
}
