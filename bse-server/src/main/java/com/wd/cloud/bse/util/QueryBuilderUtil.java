package com.wd.cloud.bse.util;

import com.wd.cloud.bse.es.QueryBuilderStrategyI;
import com.wd.cloud.bse.vo.QueryCondition;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.List;


public class QueryBuilderUtil {


    public static QueryBuilder convertQueryBuilder(List<QueryCondition> list) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for (QueryCondition condition : list) {
        	if("orgQuery".equals(condition.getBeanName().trim())) {
        		continue;
        	}
            QueryBuilderStrategyI strategy = (QueryBuilderStrategyI) SpringContextUtil.getBean(condition.getBeanName().trim());
//			QueryBuilder queryBuilder = QueryBuilders.termsQuery(condition.getFieldFlag(), condition.getValues());;
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
        	if("orgQuery".equals(condition.getBeanName().trim())) {
        		QueryBuilderStrategyI strategy = (QueryBuilderStrategyI) SpringContextUtil.getBean(condition.getBeanName().trim());
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
