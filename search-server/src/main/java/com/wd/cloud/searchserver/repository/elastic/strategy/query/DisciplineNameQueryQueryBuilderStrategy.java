package com.wd.cloud.searchserver.repository.elastic.strategy.query;


import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Component;

import com.wd.cloud.searchserver.repository.elastic.strategy.QueryBuilderStrategyI;

@Component("disciplineName")
public class DisciplineNameQueryQueryBuilderStrategy implements QueryBuilderStrategyI {

	@Override
	public QueryBuilder execute(String value, Object otherConstraint) {
		return QueryBuilders.fuzzyQuery("shouLu.detailList.subjects", value).fuzziness(Fuzziness.AUTO);
	}

}
