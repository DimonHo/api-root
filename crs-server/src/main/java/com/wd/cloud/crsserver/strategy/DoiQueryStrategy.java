package com.wd.cloud.crsserver.strategy;

import com.weidu.commons.search.QueryBuilderStrategy;
import com.weidu.commons.search.SearchField;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public class DoiQueryStrategy implements QueryBuilderStrategy {

	@Override
	public QueryBuilder build(SearchField field) throws RuntimeException {
		String value = field.getValue();
		if (StringUtils.isBlank(value)) {
			return null;
		}
		return QueryBuilders.boolQuery()
				.should(QueryBuilders.termsQuery(field.getFieldName(), value).boost(10))
				.should(QueryBuilders.prefixQuery(field.getFieldName(), value).boost(1))
				.minimumNumberShouldMatch(1);
	}

}
