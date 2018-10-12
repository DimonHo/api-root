package com.wd.cloud.reportanalysis.es.build.query;


import com.wd.cloud.reportanalysis.entity.QueryCondition;
import com.wd.cloud.reportanalysis.es.build.QueryBuilderStrategyI;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Component;

/**
 * 范围检索
 *
 * @author Administrator
 */
@Component("source")
public class SourceQueryBuildStrategy implements QueryBuilderStrategyI {


    @Override
    public QueryBuilder execute(QueryCondition queryCondition) {
        String value = queryCondition.getValue();
        if ("WOS".equals(value)) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.should(QueryBuilders.termQuery("shoulu", "SCI-E"));
            boolQueryBuilder.should(QueryBuilders.termQuery("shoulu", "SSCI"));
            boolQueryBuilder.should(QueryBuilders.termQuery("shoulu", "CPCI-S"));
            return boolQueryBuilder;
        }
        return QueryBuilders.termQuery("shoulu", value.trim());
    }


}
