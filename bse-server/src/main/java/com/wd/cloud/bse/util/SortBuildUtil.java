package com.wd.cloud.bse.util;


import com.wd.cloud.bse.es.QueryBuildContext;
import com.wd.cloud.bse.es.SortBuilderStrategyI;
import com.wd.cloud.bse.vo.SortCondition;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 数据排序组合器
 *
 * @author yangshuaifei
 */
@Component
public class SortBuildUtil {

    @Autowired
    QueryBuildContext queryBuilderStrategyContext;

    /**
     * 排序
     *
     * @param sortConditions
     * @return
     */
    public List<SortBuilder> buildSorts(List<SortCondition> sortConditions) {
        Map<String, SortBuilderStrategyI> map = queryBuilderStrategyContext.getSortBuilders();
        List<SortBuilder> list = new ArrayList<>();
        SortBuilder sortBuild = null;
        if (sortConditions != null) {
            for (SortCondition sortCondition : sortConditions) {
                SortBuilderStrategyI strategy = map.get(sortCondition.getField());
                if (strategy != null) {
                    sortBuild = strategy.build(sortCondition);
                    list.add(sortBuild);
                }
            }
        }
        return list;
    }

}
