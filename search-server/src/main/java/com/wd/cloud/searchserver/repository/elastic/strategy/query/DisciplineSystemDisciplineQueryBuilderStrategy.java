package com.wd.cloud.searchserver.repository.elastic.strategy.query;


import com.wd.cloud.searchserver.repository.elastic.strategy.QueryBuilderStrategyI;
import org.apache.lucene.queryparser.classic.QueryParserBase;
import org.elasticsearch.index.query.*;
import org.springframework.stereotype.Component;

/**
 * 学科体系的学科检索策略
 *
 * @author pan
 */
@Component("disciplineSystemDiscipline")
public class DisciplineSystemDisciplineQueryBuilderStrategy implements QueryBuilderStrategyI {

    @Override
    public QueryBuilder execute(String value, Object otherConstraint) {
        TermQueryBuilder termType = QueryBuilders.termQuery("_type", "discipline_system");
        QueryStringQueryBuilder queryString = QueryBuilders.queryStringQuery(QueryParserBase.escape(value.toLowerCase().replaceAll("&", "").replaceAll(",", ""))).field("discipline.system").field("name").defaultOperator(Operator.AND);
        return QueryBuilders.boolQuery().must(termType).must(queryString);
    }

}
