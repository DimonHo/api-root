package com.wd.cloud.reportanalysis.es.build.query;


import com.wd.cloud.reportanalysis.entity.QueryCondition;
import com.wd.cloud.reportanalysis.es.build.QueryBuilderStrategyI;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 范围检索
 *
 * @author Administrator
 */
@Component("year")
public class YearQueryBuildStrategy implements QueryBuilderStrategyI {


    @Override
    public QueryBuilder execute(QueryCondition queryCondition) {
    	String value = queryCondition.getValue();
    	BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
    	boolQueryBuilder.should(QueryBuilders.termQuery("year", value));
        return boolQueryBuilder;
    }


}
