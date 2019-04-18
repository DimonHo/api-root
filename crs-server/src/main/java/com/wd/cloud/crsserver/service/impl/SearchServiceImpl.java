package com.wd.cloud.crsserver.service.impl;

import com.wd.cloud.crsserver.service.SearchServcie;
import com.weidu.commons.search.*;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.joda.time.convert.AbstractConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: He Zhigang
 * @Date: 2019/4/16 21:04
 * @Description:
 */
@Slf4j
@Service("searchService")
public class SearchServiceImpl extends AbstractSearch implements SearchServcie {

    @Autowired
    private SearchContext searchContext;

    @Autowired
    BuilderStrategyContext builderStrategyContext;

    @Autowired
    TransportClient transportClient;

    @Override
    public Map<String, Object> getById(String index, String id) {
        return transportClient.prepareGet().setIndex(index).setId(id).get().getSource();
    }

    @Override
    public Map<String, Map<String, Object>> findByIds(String index, String[] ids) {
        SearchResponse response = transportClient.prepareSearch(index).setQuery(QueryBuilders.idsQuery().addIds(ids)).setSize(ids.length)
                .get();
        SearchHit[] hits = response.getHits().getHits();
        Map<String,Map<String,Object>> list  = new HashMap<String,Map<String,Object>>();
        if(hits!=null){
            Map<String,Object> source;
            for(SearchHit hit : hits){
                source  = hit.getSource();
                source.put("_id", hit.getId());
                list.put(hit.getId(), source);
            }
        }
        return list;
    }

    @Override
    public void updateById(String index, String type, String id, Map source) {
        transportClient.prepareUpdate(index,type,id).setDoc(source).get();
    }

    @Override
    public void index(String index, String type, XContentBuilder source) {
        transportClient.prepareIndex(index,type).setSource(source).get();
    }

    @Override
    public SearchPager<Map<String, Object>> search(SearchCondition searchCondition) {
        SearchRequestBuilder searchRequestBuilder = RequestBuilder.initSearchRequestBuilder(builderStrategyContext,transportClient,searchCondition);
        log.info("查询语句{}", searchRequestBuilder.toString());
        SearchResponse resp = searchRequestBuilder.get();
        SearchPager<Map<String, Object>> page = this.covertSearchResult(resp);
        return page;
    }


    @Override
    public SearchContext getSearchContext() {
        return searchContext;
    }
}
