package com.wd.cloud.pdfsearchserver.repository.imp;

import com.wd.cloud.pdfsearchserver.repository.ElasticRepository;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("elasticRepository")
public class ElasticRepositoryImp implements ElasticRepository {

    final
    TransportClient transportClient;

    @Autowired
    public ElasticRepositoryImp(TransportClient transportClient) {
        this.transportClient = transportClient;
    }

    @Override
    public SearchResponse queryByName(String indexName, String type, QueryBuilder qeryBuilder) {
        return transportClient.prepareSearch(indexName).setTypes(type).setQuery(qeryBuilder).setSize(10).execute().actionGet();
    }

    public SearchResponse queryAllTypeByQueryBuilder(String indexName, QueryBuilder qeryBuilder){
        return transportClient.prepareSearch(indexName).setQuery(qeryBuilder).setSize(10).execute().actionGet();
    }
}
