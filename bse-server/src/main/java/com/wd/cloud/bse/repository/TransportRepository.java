package com.wd.cloud.bse.repository;

import java.util.Iterator;
import java.util.List;

import org.elasticsearch.action.search.ClearScrollRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wd.cloud.bse.repository.TransportRepository.BulkInterator;
import com.wd.cloud.bse.util.ClientFactory;
import com.wd.cloud.bse.util.FacetBuildUtil;
import com.wd.cloud.bse.util.RequestBuildUtil;
import com.wd.cloud.bse.vo.SearchCondition;

@Component
public class TransportRepository {
	
	 @Autowired
	 TransportClient transportClient;
	 
	 @Autowired
	 RequestBuildUtil requestBuildUtil;
	 
	 @Autowired
	 FacetBuildUtil facetBuildUtil;
	 /**
	  * 根据条件查询列表
	  * @param condition
	  * @return
	  */
	 public SearchResponse query(SearchCondition condition) {
		 SearchRequestBuilder searchRequest = transportClient.prepareSearch(condition.getIndexName()).setTypes(condition.getTypes());
		 requestBuildUtil.build(condition, searchRequest);
		 System.out.println(searchRequest.toString());
		 SearchResponse response = searchRequest.get();
		 return response;
	 }
	 
	 /**
	  * 根据条件查询列表
	  * @param condition
	  * @return
	  */
	 public Iterator<SearchHit> query(QueryBuilder queryBuilder, QueryBuilder filterBuilder, List<AbstractAggregationBuilder> aggregations,String index, String type) {
		 SearchRequestBuilder searchRequest = transportClient.prepareSearch(index).setTypes(type);
		 searchRequest.setSearchType(SearchType.DEFAULT);
		 if (null != queryBuilder && null != filterBuilder) {
			 searchRequest.setQuery(queryBuilder).setPostFilter(filterBuilder);
		 } else if (null != queryBuilder) {
			 searchRequest.setQuery(queryBuilder);
		 } else if (null != filterBuilder) {
			 searchRequest.setPostFilter(filterBuilder);
		 }
		 if (null != aggregations) {
			 for (AbstractAggregationBuilder aggregation : aggregations) {
				 searchRequest.addAggregation(aggregation);
			 }
		 }
		 searchRequest//.setSearchType(SearchType.SCAN)
		   .setScroll(TimeValue.timeValueMinutes(60));
		 System.out.println(searchRequest.toString());
		 SearchResponse response = searchRequest.get();
		 return new BulkInterator(response);
	 }
	 /**
	  * 根据id查询详细
	  * @param id
	  * @param index
	  * @return
	  */
	public SearchResponse queryByIds(String[] id,String index) {
		SearchResponse resp = transportClient.prepareSearch(index).setQuery(QueryBuilders.idsQuery().addIds(id)).execute().actionGet();
		return resp;
	}
	
	/**
	 * 根据条件查询id数组里的数据（）
	 * @param id
	 * @param condition
	 * @return
	 */
	public SearchResponse search(String[] id,SearchCondition condition) {
		SearchRequestBuilder searchRequest = transportClient.prepareSearch(condition.getIndexName()).setTypes(condition.getTypes());
		if(id != null) {
			searchRequest.setQuery(QueryBuilders.idsQuery().addIds(id));
		}
		if(condition != null) {
			requestBuildUtil.build(condition, searchRequest,id);
		}
		System.out.println(searchRequest.toString());
		SearchResponse resp = searchRequest.execute().actionGet();
		return resp;
	}
	
	 public class BulkInterator implements Iterator<SearchHit>{
			
			private SearchHit[] hits;
			
			private String scrollId ;
			
			private int count =0;
			
			private int index =0 ;
			
			private int allCount  = 0 ;
			
			public BulkInterator(SearchResponse response){
				hits= response.getHits().getHits();
				count = hits.length;
				this.scrollId = response.getScrollId();
			}

			@Override
			public boolean hasNext() {
				if(count == index){
					SearchResponse searchResponse = transportClient.prepareSearchScroll(scrollId)
		    		        .setScroll(TimeValue.timeValueMinutes(60)).get();
					count = searchResponse.getHits().getHits().length;
					if(count >0){//还有数据
						hits = searchResponse.getHits().getHits();
						index =0 ;
						return true;
					}
				}else if(index < count){
					return true;
				}
				//清楚滚动ID
				ClearScrollRequestBuilder clearScrollRequestBuilder = transportClient.prepareClearScroll();
				clearScrollRequestBuilder.addScrollId(scrollId);
				clearScrollRequestBuilder.get();
				return false;
			}

			@Override
			public SearchHit next() {
				allCount++;
				return hits[index++];
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
			
		}

}
