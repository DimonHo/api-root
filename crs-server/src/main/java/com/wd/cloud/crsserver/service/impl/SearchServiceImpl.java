package com.wd.cloud.crsserver.service.impl;

import com.wd.cloud.crsserver.service.SearchServcie;
import com.weidu.commons.search.*;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.joda.time.convert.AbstractConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
