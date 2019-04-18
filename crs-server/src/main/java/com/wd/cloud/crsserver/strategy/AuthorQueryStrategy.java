package com.wd.cloud.crsserver.strategy;

import com.weidu.commons.search.QueryBuilderStrategy;
import com.weidu.commons.search.SearchField;
import com.weidu.commons.util.ChineseUtil;
import com.weidu.commons.util.PinYinUtil;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public class AuthorQueryStrategy implements QueryBuilderStrategy {

	@Override
	public QueryBuilder build(SearchField queryCondition) throws RuntimeException {
		String value = queryCondition.getValue();
		if(ChineseUtil.isChinese(value)){
			String[] pins = PinYinUtil.getPinYins(value);
			if(pins.length >1){
				BoolQueryBuilder boolQuery =  QueryBuilders.boolQuery();
				String firstName = pins[0],secondName=pins[1],simpleSN=pins[1].charAt(0)+""; //姓、名、简明
				for(int i=2;i<pins.length;i++){
					secondName += " "+pins[i];
					simpleSN += pins[i].charAt(0);
				}
				boolQuery.should(QueryBuilders.queryStringQuery(firstName+" "+ simpleSN).field("authorAnalyzed").minimumShouldMatch("100%"))
				.should(QueryBuilders.queryStringQuery(firstName+" "+ secondName).field("authorAnalyzed").minimumShouldMatch("100%"))
				//姓 名, 名 姓,姓 简名, 简名 姓
				.should(QueryBuilders.termsQuery("authorFacets", firstName+" "+ simpleSN,firstName+" "+ secondName,secondName+" "+ firstName,simpleSN+" "+ firstName))
				.minimumNumberShouldMatch(1);
				return boolQuery;
			}
		}
		return QueryBuilders.boolQuery().should(QueryBuilders.queryStringQuery(value).field("authorAnalyzed").minimumShouldMatch("100%"))
				.should(QueryBuilders.queryStringQuery(value).field("authorAnalyzed").minimumShouldMatch("100%"))
				.should(QueryBuilders.termQuery("authorFacets", value)).minimumNumberShouldMatch(1);
	}
}
