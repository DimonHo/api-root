package com.wd.cloud.bse.es.query;

import java.util.List;

import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.join.ScoreMode;
import com.wd.cloud.bse.es.QueryBuilderStrategyI;
import com.wd.cloud.bse.vo.QueryCondition;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component("relationSubjectQuery")
public class RelationSubjectQueryBuildStrategy implements QueryBuilderStrategyI {

	@Override
    public QueryBuilder execute(QueryCondition queryCondition) {
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
			String field = queryCondition.getFieldFlag();
			String value = queryCondition.getValue();
			QueryBuilder subBoolQueryBuilder = QueryBuilders.termQuery(field, value.trim());
			boolQueryBuilder.must(subBoolQueryBuilder);
		
		return boolQueryBuilder;
		
	}


	
}
