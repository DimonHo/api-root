package com.wd.cloud.bse.repository;

import java.util.Iterator;
import java.util.List;

import org.elasticsearch.action.search.ClearScrollRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransportRepository {
	
	 @Autowired
	 TransportClient transportClient;
	 
	 public SearchResponse query(QueryBuilder queryBuilder, QueryBuilder filterBuilder, List<AggregationBuilder> aggregations,SortBuilder sort, String type,int from,int size) {
		 SearchRequestBuilder searchRequest = transportClient.prepareSearch("res").setTypes("paper").setFrom(from).setSize(size);
		 searchRequest.setSearchType(SearchType.DEFAULT);
		 if (null != queryBuilder && null != filterBuilder) {
			 searchRequest.setQuery(queryBuilder).setPostFilter(filterBuilder);
		 } else if (null != queryBuilder) {
			 searchRequest.setQuery(queryBuilder);
		 } else if (null != filterBuilder) {
			 searchRequest.setPostFilter(filterBuilder);
		 }
		 if (null != aggregations) {
			 for (AggregationBuilder aggregation : aggregations) {
				 searchRequest.addAggregation(aggregation);
			 }
		 }
		 if(null != sort) {
			 searchRequest.addSort(sort);
		 }
		 System.out.println(searchRequest.toString());
		 SearchResponse response = searchRequest.get();
		 return response;
	 }
	 
	 
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
