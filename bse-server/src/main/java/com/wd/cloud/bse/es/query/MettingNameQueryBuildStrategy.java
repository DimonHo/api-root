package com.wd.cloud.bse.es.query;


import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Component;

import com.wd.cloud.bse.es.QueryBuilderStrategyI;
import com.wd.cloud.bse.vo.LatConstant;
import com.wd.cloud.bse.vo.QueryCondition;


/**
 * 刊名query策略
 * 
 * @author Administrator
 * 
 */
@Component("mettingNameQuery")
public class MettingNameQueryBuildStrategy implements QueryBuilderStrategyI {

	@Override
	public QueryBuilder execute(QueryCondition queryCondition) {
		String value = queryCondition.getValue();
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		
		boolQueryBuilder.should(
				QueryBuilders.nestedQuery("documents", QueryBuilders.queryStringQuery(QueryParser.escape(
						 value.trim())).defaultField("documents.meetingName").defaultOperator(Operator.AND).minimumShouldMatch(LatConstant.SEARCH_SIMILARITY_SITUABLE_MATCH), ScoreMode.Max));
		boolQueryBuilder.minimumShouldMatch(1);
		return boolQueryBuilder;
	}
}
