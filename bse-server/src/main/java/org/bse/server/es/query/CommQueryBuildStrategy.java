package org.bse.server.es.query;

import org.bse.server.es.QueryBuilderStrategyI;
import org.bse.server.vo.QueryCondition;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Component;

@Component("commTermsQuery")
public class CommQueryBuildStrategy implements QueryBuilderStrategyI {

	@Override
	public QueryBuilder execute(QueryCondition queryCondition) {
		String field = queryCondition.getFieldFlag();
		String value = queryCondition.getValue();
        return QueryBuilders.termQuery(field, value.trim());
	}

}
