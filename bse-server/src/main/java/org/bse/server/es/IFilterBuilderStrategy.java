package org.bse.server.es;


import org.bse.server.vo.QueryCondition;
import org.elasticsearch.index.query.QueryBuilder;


public interface IFilterBuilderStrategy {

	public QueryBuilder build(QueryCondition queryCondition);
}
