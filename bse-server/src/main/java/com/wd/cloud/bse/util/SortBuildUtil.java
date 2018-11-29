package com.wd.cloud.bse.util;


import com.wd.cloud.bse.es.SortBuilderStrategy;
import com.wd.cloud.bse.vo.SortCondition;
import org.elasticsearch.search.sort.SortBuilder;

public class SortBuildUtil {
	
	
	/**
	 * 排序
	 * @param elasticSearchCondition
	 * @return
	 */
	public static SortBuilder buildSorts(SortCondition sortCondition) {
		SortBuilder sortBuild = null;
		if(sortCondition != null){
			SortBuilderStrategy sortBuilder = (SortBuilderStrategy) SpringContextUtil.getBean(sortCondition.getBeanName());
			if(sortBuilder != null){
				sortBuild = sortBuilder.build(sortCondition);
			}
		}
		return sortBuild;
	}

}
