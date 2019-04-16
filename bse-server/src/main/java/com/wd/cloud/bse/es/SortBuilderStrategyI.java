package com.wd.cloud.bse.es;

import com.wd.cloud.bse.vo.SortCondition;
import org.elasticsearch.search.sort.SortBuilder;


public interface SortBuilderStrategyI {

    public SortBuilder build(SortCondition sortCondition);

}
