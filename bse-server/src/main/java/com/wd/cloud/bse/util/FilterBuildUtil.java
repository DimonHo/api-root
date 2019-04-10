package com.wd.cloud.bse.util;

import com.wd.cloud.bse.es.FilterBuilderStrategyI;
import com.wd.cloud.bse.es.QueryBuildContext;
import com.wd.cloud.bse.vo.QueryCondition;
import com.wd.cloud.bse.vo.SearchCondition;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 过滤条件组合器
 *
 * @author Administrator
 */
@Component
public class FilterBuildUtil {

    @Autowired
    QueryBuildContext queryBuilderStrategyContext;

    public QueryBuilder buildFilter(SearchCondition searchCondition) throws RuntimeException {
        Map<String, FilterBuilderStrategyI> map = queryBuilderStrategyContext.getFilterBuilders();
        BoolQueryBuilder querybuilder = QueryBuilders.boolQuery();
        BoolQueryBuilder filterBuilder = QueryBuilders.boolQuery();
        List<List<QueryCondition>> filters = searchCondition.getFilters();
        if (filters != null) {
            for (List<QueryCondition> list : filters) {
                BoolQueryBuilder andFilterBuilder = QueryBuilders.boolQuery();
                for (QueryCondition condition : list) {
                    FilterBuilderStrategyI strategy = map.get(condition.getBeanName());
                    QueryBuilder builder = strategy.build(condition);
                    switch (condition.getLogic()) {
                        case 2:
                            andFilterBuilder.should(builder);
                            break;
                        case 3:
                            andFilterBuilder.mustNot(builder);
                            break;
                        case 1:
                        default:
                            andFilterBuilder.must(builder);
                            break;
                    }
                }
                filterBuilder.should(andFilterBuilder);
            }
            querybuilder.must(filterBuilder);
        }
        if (searchCondition.getFilterConditions() != null) {
            for (QueryCondition condition : searchCondition.getFilterConditions()) {
                FilterBuilderStrategyI strategy = map.get(condition.getBeanName());
                QueryBuilder builder = strategy.build(condition);
                switch (condition.getLogic()) {
                    case 2:
                        querybuilder.should(builder);
                        break;
                    case 3:
                        querybuilder.mustNot(builder);
                        break;
                    case 1:
                    default:
                        querybuilder.must(builder);
                        break;
                }
            }
        }
        return querybuilder;
    }

}
