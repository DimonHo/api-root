package com.wd.cloud.bse.es.fliter;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Component;

import com.wd.cloud.bse.es.FacetFilterBuilder;
import com.wd.cloud.bse.vo.QueryCondition;


/**
 * 通用的词条过滤器
 * @author Administrator
 *
 */
@Component("commTermsFilter")
public class CommTermsFilterBuildStrategy implements FacetFilterBuilder {

	@Override
	public QueryBuilder build(QueryCondition condition) {
		return QueryBuilders.termsQuery(condition.getFieldFlag(), condition.getValues());
	}
	
	@Override
	public String getPath() {
		return "documents";
	}

}
