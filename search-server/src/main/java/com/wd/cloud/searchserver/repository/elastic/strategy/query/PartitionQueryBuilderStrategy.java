package com.wd.cloud.searchserver.repository.elastic.strategy.query;


import com.wd.cloud.searchserver.repository.elastic.strategy.QueryBuilderStrategyI;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Component;

/**
 * 期刊所处分区查询
 *
 * @author pan
 */
@Component("partition")
public class PartitionQueryBuilderStrategy implements QueryBuilderStrategyI {

    @Override
    public QueryBuilder execute(String value, Object otherConstraint) {
        return QueryBuilders.termQuery("shouLu.detailList.partition", value);
    }

}
