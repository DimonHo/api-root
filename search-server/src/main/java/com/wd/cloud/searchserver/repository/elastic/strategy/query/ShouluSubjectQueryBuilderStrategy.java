package com.wd.cloud.searchserver.repository.elastic.strategy.query;


import com.wd.cloud.searchserver.repository.elastic.strategy.QueryBuilderStrategyI;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Component;

@Component("shouLuSubjects")
public class ShouluSubjectQueryBuilderStrategy implements QueryBuilderStrategyI {

    @Override
    public QueryBuilder execute(String value, Object otherConstraint) {
        return QueryBuilders.termQuery("shouLu.detailList.subject", value);
    }

}
