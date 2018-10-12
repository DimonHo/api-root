package com.wd.cloud.reportanalysis.es.build.query;


import com.wd.cloud.reportanalysis.entity.QueryCondition;
import com.wd.cloud.reportanalysis.es.build.QueryBuilderStrategyI;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Component;

/**
 * 范围检索
 *
 * @author Administrator
 */
@Component("scid")
public class OrgQueryBuildStrategy implements QueryBuilderStrategyI {


    @Override
    public QueryBuilder execute(QueryCondition queryCondition) {
        String value = queryCondition.getValue();
        return QueryBuilders.termQuery("org", value.trim());
    }


}
