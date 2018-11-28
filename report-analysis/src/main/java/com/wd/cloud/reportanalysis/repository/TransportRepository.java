package com.wd.cloud.reportanalysis.repository;

import java.util.List;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransportRepository {

    @Autowired
    TransportClient transportClient;


    public SearchResponse query(QueryBuilder queryBuilder, QueryBuilder filterBuilder, List<AbstractAggregationBuilder> aggregations, String type) {
        SearchRequestBuilder searchRequest = transportClient.prepareSearch("resource").setTypes(type);
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
		System.out.println(searchRequest.toString());
        SearchResponse response = searchRequest.get();
        return response;
    }

}
