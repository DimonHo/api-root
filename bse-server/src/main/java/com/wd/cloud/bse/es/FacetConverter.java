package com.wd.cloud.bse.es;


import org.elasticsearch.search.aggregations.Aggregation;

import com.wd.cloud.bse.vo.FacetResult;

/**
 * 分类统计转换
 * @author Administrator
 *
 */
public interface FacetConverter {
	
	public FacetResult convert(Aggregation facet);

}
