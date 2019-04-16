package com.wd.cloud.bse.es;


import com.wd.cloud.bse.vo.FacetResult;
import org.elasticsearch.search.aggregations.Aggregation;

/**
 * 分类统计转换
 *
 * @author Administrator
 */
public interface FacetConverter {

    public FacetResult convert(Aggregation facet);

}
