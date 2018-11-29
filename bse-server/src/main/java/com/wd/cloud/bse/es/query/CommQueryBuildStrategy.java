package com.wd.cloud.bse.es.query;

import com.wd.cloud.bse.es.QueryBuilderStrategyI;
import com.wd.cloud.bse.vo.QueryCondition;
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
