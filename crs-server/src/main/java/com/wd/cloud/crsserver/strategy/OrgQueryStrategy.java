package com.wd.cloud.crsserver.strategy;

import com.weidu.commons.search.QueryBuilderStrategy;
import com.weidu.commons.search.SearchField;
import com.weidu.commons.util.ChineseUtil;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public class OrgQueryStrategy implements QueryBuilderStrategy {

	@Override
	public QueryBuilder build(SearchField field) throws RuntimeException {
		String value = field.getValue();
		if(ChineseUtil.isChinese(value)){//如果是中文机构
			
		}
		return QueryBuilders.boolQuery().must(QueryBuilders.queryStringQuery(value).field("affiliationAnalyzed").minimumShouldMatch("100%"))
				.should(QueryBuilders.matchPhraseQuery("affiliationAnalyzed",value));
	}

}
