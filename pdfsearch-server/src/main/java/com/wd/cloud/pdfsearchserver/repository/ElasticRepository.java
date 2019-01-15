package com.wd.cloud.pdfsearchserver.repository;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;

public interface ElasticRepository {
    public SearchResponse queryByName(String indexName, String type, QueryBuilder qeryBuilder);
}
