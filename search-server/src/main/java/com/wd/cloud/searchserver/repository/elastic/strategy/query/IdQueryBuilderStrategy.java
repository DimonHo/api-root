package com.wd.cloud.searchserver.repository.elastic.strategy.query;


import com.wd.cloud.searchserver.repository.elastic.strategy.QueryBuilderStrategyI;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Component;

@Component("id")
public class IdQueryBuilderStrategy implements QueryBuilderStrategyI {

    @Override
    public QueryBuilder execute(String value, Object otherConstraint) {
        return QueryBuilders.idsQuery().addIds(value);
    }

}
