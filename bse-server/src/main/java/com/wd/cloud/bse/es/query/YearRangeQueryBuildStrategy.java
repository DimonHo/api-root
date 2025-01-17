package com.wd.cloud.bse.es.query;


import com.wd.cloud.bse.es.QueryBuilderStrategyI;
import com.wd.cloud.bse.vo.QueryCondition;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * 范围检索
 *
 * @author Administrator
 */
@Component("rangeQuery")
public class YearRangeQueryBuildStrategy implements QueryBuilderStrategyI {

    @Override
    public QueryBuilder execute(QueryCondition queryCondition) {
        List<String> values = queryCondition.getValues();
        if (values.size() > 2) {
            throw new RuntimeException("RangeFilter参数必须小于有两个");
        }
        Integer a = null, b = null;
        if (values.size() == 1) {
            String[] items = StringUtils.split(queryCondition.getValue(), "-");
            a = Integer.parseInt(items[0]);
            if (items.length > 1) {
                b = Integer.parseInt(items[0]);
            } else {
                b = Integer.MAX_VALUE;
            }
        } else {
            a = Integer.parseInt(values.get(0));
            b = Integer.parseInt(values.get(1));
        }
        return QueryBuilders.rangeQuery("year").from(Math.min(a, b)).to(Math.max(a, b));
    }


}
