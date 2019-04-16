package com.wd.cloud.bse.es.query;

import com.wd.cloud.bse.es.QueryBuilderStrategyI;
import com.wd.cloud.bse.vo.QueryCondition;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Component;

@Component("relationSubjectQuery")
public class RelationSubjectQueryBuildStrategy implements QueryBuilderStrategyI {

    @Override
    public QueryBuilder execute(QueryCondition queryCondition) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        String field = queryCondition.getFieldFlag();
        String value = queryCondition.getValue();
        QueryBuilder subBoolQueryBuilder = QueryBuilders.termQuery(field, value.trim());
        boolQueryBuilder.must(subBoolQueryBuilder);

        return boolQueryBuilder;

    }


}
