package com.wd.cloud.bse.es;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.wd.cloud.bse.es.FilterBuilderStrategyI;
import com.wd.cloud.bse.es.QueryBuilderStrategyI;
import com.wd.cloud.bse.es.SortBuilderStrategyI;
import com.wd.cloud.bse.vo.FacetField;

@Component
public class QueryBuildContext {
	
	/**查询构建器*/
	private Map<String,QueryBuilderStrategyI> queryBuilders;
	
	/**过滤器构建器*/
	private Map<String,FilterBuilderStrategyI> filterBuilders;
	
	private Map<String,List<FacetField>> facets;
	
	private Map<String,SortBuilderStrategyI> sortBuilders;

	public Map<String, QueryBuilderStrategyI> getQueryBuilders() {
		return queryBuilders;
	}

	public void setQueryBuilders(Map<String, QueryBuilderStrategyI> queryBuilders) {
		this.queryBuilders = queryBuilders;
	}

	public Map<String, FilterBuilderStrategyI> getFilterBuilders() {
		return filterBuilders;
	}

	public void setFilterBuilders(Map<String, FilterBuilderStrategyI> filterBuilders) {
		this.filterBuilders = filterBuilders;
	}

	public Map<String, List<FacetField>> getFacets() {
		return facets;
	}

	public void setFacets(Map<String, List<FacetField>> facets) {
		this.facets = facets;
	}

	public Map<String, SortBuilderStrategyI> getSortBuilders() {
		return sortBuilders;
	}

	public void setSortBuilders(Map<String, SortBuilderStrategyI> sortBuilders) {
		this.sortBuilders = sortBuilders;
	}
	
	

}
