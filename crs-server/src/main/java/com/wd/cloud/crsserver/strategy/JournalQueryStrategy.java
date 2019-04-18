package com.wd.cloud.crsserver.strategy;

import com.weidu.commons.search.QueryBuilderStrategy;
import com.weidu.commons.search.SearchField;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

public class JournalQueryStrategy implements QueryBuilderStrategy {

	// ISSN,EISSN格式
	private static final Pattern ISSN_REGEX = Pattern.compile("^[\\d]{4}\\-[\\d\\w]{4}$");

	@Override
	public QueryBuilder build(SearchField queryCondition) throws RuntimeException {
		String value = queryCondition.getValue();
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		BoolQueryBuilder defaultBuilder = QueryBuilders.boolQuery();
		if (ISSN_REGEX.matcher(value).find()) {
			defaultBuilder.should(QueryBuilders.termQuery("journal.issn", value).boost(5f))
					.should(QueryBuilders.termQuery("journal.eissn", value).boost(5f));
		}
		defaultBuilder.should(QueryBuilders.queryStringQuery(value).field("journal.name").minimumShouldMatch("100%"))
				.should(QueryBuilders.queryStringQuery(value).field("journal.nameAbb").minimumShouldMatch("100%"))
				.should(QueryBuilders.matchPhraseQuery("journal.name", value).boost(5f))
				.should(QueryBuilders.matchPhraseQuery("journal.nameAbb", value).boost(5f));
		defaultBuilder.minimumNumberShouldMatch(1);
		return defaultBuilder;
	}

}
