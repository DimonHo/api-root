package com.wd.cloud.bse.util;

import com.wd.cloud.bse.es.QueryBuildContext;
import com.wd.cloud.bse.es.QueryBuilderStrategyI;
import com.wd.cloud.bse.vo.QueryCondition;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Query查询组合器
 * @author yangshuaifei
 *
 */
@Component
public class QueryBuilderUtil {
	
	@Autowired
	QueryBuildContext queryBuilderStrategyContext;
	
    public BoolQueryBuilder convertQueryBuilder(List<QueryCondition> list) {
    	Map<String,QueryBuilderStrategyI> map = queryBuilderStrategyContext.getQueryBuilders();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if(list == null) {
            return boolQueryBuilder;
        }
        for (QueryCondition condition : list) {
        	if("org".equals(condition.getFieldFlag().trim()) || "relationSubject".equals(condition.getFieldFlag().trim())) {
        		continue;
        	}
        	QueryBuilderStrategyI strategy = map.get(condition.getFieldFlag());
            QueryBuilder queryBuilder = strategy.execute(condition);
            switch (condition.getLogic()) {
                case 1: {
                    boolQueryBuilder.must(queryBuilder);
                    break;
                }
                case 2: {
                    boolQueryBuilder.should(queryBuilder);
                    break;
                }
                default: {
                    boolQueryBuilder.mustNot(queryBuilder);
                }
            }
        }
        BoolQueryBuilder orgBuilder = QueryBuilders.boolQuery();
        for (QueryCondition condition : list) {
        	if("org".equals(condition.getFieldFlag().trim())|| "relationSubject".equals(condition.getFieldFlag().trim())) {
        		QueryBuilderStrategyI strategy = map.get(condition.getFieldFlag());
        		QueryBuilder queryBuilder = strategy.execute(condition);
                switch (condition.getLogic()) {
                    case 1: {
                    	orgBuilder.must(queryBuilder);
                        break;
                    }
                    case 2: {
                    	orgBuilder.should(queryBuilder);
                        break;
                    }
                    default: {
                    	orgBuilder.mustNot(queryBuilder);
                    }
                }
        	}
        }
        boolQueryBuilder.must(orgBuilder);
        return boolQueryBuilder;

    }

}
