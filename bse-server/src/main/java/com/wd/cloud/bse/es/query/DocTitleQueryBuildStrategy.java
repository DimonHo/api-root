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

@Component("titleQuery")
public class DocTitleQueryBuildStrategy implements QueryBuilderStrategyI {

	public QueryBuilder execute(QueryCondition queryCondition) {
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		String field = queryCondition.getFieldFlag();
		String author = queryCondition.getValue();
			
		BoolQueryBuilder subBoolQueryBuilder = QueryBuilders.boolQuery();
		subBoolQueryBuilder.should(QueryBuilders.nestedQuery("documents",
			QueryBuilders.queryStringQuery(QueryParser.escape(author))
			.defaultField("documents.docTitle").minimumShouldMatch("75%").defaultOperator(Operator.AND)
			, ScoreMode.Max));
		subBoolQueryBuilder.minimumShouldMatch(1);
		
		boolQueryBuilder.must(subBoolQueryBuilder);
					
		return boolQueryBuilder;
		
	}


	
}
