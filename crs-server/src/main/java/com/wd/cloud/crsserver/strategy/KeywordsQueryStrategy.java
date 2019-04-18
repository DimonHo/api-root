package com.wd.cloud.crsserver.strategy;

import com.weidu.commons.search.QueryBuilderStrategy;
import com.weidu.commons.search.SearchField;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.util.StringUtils;

public class KeywordsQueryStrategy implements QueryBuilderStrategy {

	@Override
	public QueryBuilder build(SearchField field) throws RuntimeException {
		String value = field.getValue();
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		return QueryBuilders.boolQuery().must(QueryBuilders.queryStringQuery(value).field(field.getFieldName()).minimumShouldMatch("80%"))
				.should(QueryBuilders.matchPhraseQuery(field.getFieldName(), value));
	}

}
