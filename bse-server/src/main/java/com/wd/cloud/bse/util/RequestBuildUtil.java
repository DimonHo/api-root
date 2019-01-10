package com.wd.cloud.bse.util;

import java.util.List;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wd.cloud.bse.vo.SearchCondition;

@Component
public class RequestBuildUtil {
	
	@Autowired
	QueryBuilderUtil queryBuilderUtil;
	
	@Autowired
	FilterBuildUtil filterBuildUtil;
	
	@Autowired
	SortBuildUtil sortBuildUtil;
	
	@Autowired
	FacetBuildUtil facetBuildUtil;
	
	public void build(SearchCondition condition,SearchRequestBuilder searchRequest) {
		BoolQueryBuilder queryBuilder = queryBuilderUtil.convertQueryBuilder(condition.getQueryConditions());
		QueryBuilder filterBuilder = filterBuildUtil.buildFilter(condition);
		List<AggregationBuilder> aggregations  = facetBuildUtil.buildFacets(condition);
		List<SortBuilder> sort = sortBuildUtil.buildSorts(condition.getSorts());
		searchRequest.setSearchType(SearchType.DEFAULT);
		searchRequest.setFrom(condition.getFrom()).setSize(condition.getSize());
		if (null != queryBuilder && null != filterBuilder) {
			queryBuilder.filter(filterBuilder);
			searchRequest.setQuery(queryBuilder);
		} else if (null != queryBuilder) {
			searchRequest.setQuery(queryBuilder);
		} else if (null != filterBuilder) {
			queryBuilder = QueryBuilders.boolQuery();
			queryBuilder.filter(filterBuilder);
			searchRequest.setQuery(queryBuilder);
		}
		if (null != aggregations) {
			for (AggregationBuilder aggregation : aggregations) {
				searchRequest.addAggregation(aggregation);
			}
		}
		if(null != sort) {
			for (SortBuilder sortBuilder : sort) {
				searchRequest.addSort(sortBuilder);
			}
		}
	}
	
	public void build(SearchCondition condition,SearchRequestBuilder searchRequest,String[] id) {
		BoolQueryBuilder queryBuilder = queryBuilderUtil.convertQueryBuilder(condition.getQueryConditions());
		if(id != null) {
			queryBuilder.must(QueryBuilders.idsQuery().addIds(id));
		}
		QueryBuilder filterBuilder = filterBuildUtil.buildFilter(condition);
		List<AggregationBuilder> aggregations  = facetBuildUtil.buildFacets(condition);
		List<SortBuilder> sort = sortBuildUtil.buildSorts(condition.getSorts());
		searchRequest.setSearchType(SearchType.DEFAULT);
		searchRequest.setFrom(condition.getFrom()).setSize(condition.getSize());
		if (null != queryBuilder && null != filterBuilder) {
			queryBuilder.filter(filterBuilder);
			searchRequest.setQuery(queryBuilder);
		} else if (null != queryBuilder) {
			searchRequest.setQuery(queryBuilder);
		} else if (null != filterBuilder) {
			queryBuilder = QueryBuilders.boolQuery();
			queryBuilder.filter(filterBuilder);
			searchRequest.setQuery(queryBuilder);
		}
		if (null != aggregations) {
			for (AggregationBuilder aggregation : aggregations) {
				searchRequest.addAggregation(aggregation);
			}
		}
		if(null != sort) {
			for (SortBuilder sortBuilder : sort) {
				searchRequest.addSort(sortBuilder);
			}
		}
	}

}
