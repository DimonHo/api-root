package org.bse.server.util;


import org.bse.server.es.SortBuilderStrategy;
import org.bse.server.vo.SortCondition;
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
