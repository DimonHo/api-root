package com.wd.cloud.crsserver.strategy;

import com.weidu.commons.search.QueryBuilderStrategy;
import com.weidu.commons.search.SearchField;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.util.StringUtils;

public class TitleQueryStrategy implements QueryBuilderStrategy {

	@Override
	public QueryBuilder build(SearchField queryCondition) throws RuntimeException {
		String value = queryCondition.getValue();
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		return QueryBuilders.boolQuery().must(QueryBuilders.queryStringQuery(value).field(queryCondition.getFieldName()).minimumShouldMatch("80%"));
				//.should(QueryBuilders.matchPhraseQuery(queryCondition.getFieldName(), value))
				//.minimumNumberShouldMatch(1);
	}

}
