package com.wd.cloud.bse.es.query;

import com.wd.cloud.bse.es.QueryBuilderStrategyI;
import com.wd.cloud.bse.vo.QueryCondition;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Component;

@Component("commDocTermsQuery")
public class CommDocQueryBuildStrategy implements QueryBuilderStrategyI {

    @Override
    public QueryBuilder execute(QueryCondition queryCondition) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        String field = queryCondition.getFieldFlag();
        String value = queryCondition.getValue();

        BoolQueryBuilder subBoolQueryBuilder = QueryBuilders.boolQuery();
        subBoolQueryBuilder.should(QueryBuilders.nestedQuery("documents",
                QueryBuilders.queryStringQuery(QueryParser.escape(value))
                        .defaultField("documents." + field).minimumShouldMatch("80%").defaultOperator(Operator.AND)
                , ScoreMode.Max));
        subBoolQueryBuilder.minimumShouldMatch(1);

        boolQueryBuilder.must(subBoolQueryBuilder);

        return boolQueryBuilder;
    }

}
