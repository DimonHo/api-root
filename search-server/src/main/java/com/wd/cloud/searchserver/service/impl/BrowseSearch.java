package com.wd.cloud.searchserver.service.impl;

import com.wd.cloud.searchserver.service.BrowseSearchI;
import com.wd.cloud.searchserver.util.DateUtil;
import com.wd.cloud.searchserver.util.DoubleUtil;
import com.wd.cloud.searchserver.util.EsKey;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.InternalDateHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.stats.Stats;
import org.elasticsearch.search.aggregations.metrics.stats.StatsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author He Zhigang
 * @date 2018/11/12 0012
 * @Description:
 */
@Component("browseSearch")
public class BrowseSearch implements BrowseSearchI {

    @Autowired
    TransportClient transportClient;

    @Override
    public Map<String, Object> indexInfo(String school, String beginTime, String endTime) {
        SearchResponse searchResponse  = null;
        RangeQueryBuilder filterBuilder =  QueryBuilders.rangeQuery("lastTime");
		filterBuilder.from(beginTime).to(endTime);
        BoolQueryBuilder q = QueryBuilders.boolQuery().must(filterBuilder);
        if(!StringUtils.isEmpty(school) && !"all".equals(school)) {
            QueryBuilder queryBuilder = QueryBuilders.termQuery("schoolFlag", school);
            q.must(queryBuilder);
        }

            QueryBuilder typeBuilder = QueryBuilders.termQuery("type", 0);
            q.must(typeBuilder);


        StatsAggregationBuilder countAggregation =  AggregationBuilders
                .stats("count").field("pageNum") ;

        SumAggregationBuilder aggregation =  AggregationBuilders
                .sum("pageNum").field("pageNum");

        SumAggregationBuilder timeAggregation =  AggregationBuilders
                .sum("time").field("time");

        TermsAggregationBuilder ipTermsBuilder = AggregationBuilders
                .terms("ipCount").field("ip").size(Integer.MAX_VALUE);

        TermsAggregationBuilder  uvTermsBuilder = AggregationBuilders
                .terms("uvCount").field("memberId").size(Integer.MAX_VALUE);

        DateHistogramAggregationBuilder dayTermsBuilder = AggregationBuilders.dateHistogram("day")
                .field("lastTime").format("yyyy-MM-dd").interval(60*60*24*1000).minDocCount(1);

        searchResponse = transportClient.prepareSearch(EsKey.BROWSE_INDEX).setTypes(EsKey.BROWSE_TYPE)
                .setQuery(q)
                .addAggregation(countAggregation)
                .addAggregation(aggregation)
                .addAggregation(timeAggregation)
                .addAggregation(ipTermsBuilder)
                .addAggregation(uvTermsBuilder)
                .addAggregation(dayTermsBuilder)
                .execute()
                .actionGet();

        //计算跳出率
        QueryBuilder pageNumBuilder = QueryBuilders.termQuery("pageNum", 1);
        SearchResponse jumpResponse  = transportClient.prepareSearch(EsKey.BROWSE_INDEX).setTypes(EsKey.BROWSE_TYPE)
                .setQuery(q.must(pageNumBuilder))
                .addAggregation(countAggregation)
                .execute()
                .actionGet();
        System.out.println(transportClient.prepareSearch(EsKey.BROWSE_INDEX).setTypes(EsKey.BROWSE_TYPE)
                .setQuery(q.must(pageNumBuilder))
                .addAggregation(countAggregation));
        SearchHits hits = searchResponse.getHits();
        SearchHits jumphits = jumpResponse.getHits();

        Stats counts = searchResponse.getAggregations().get("count");
        double count = counts.getCount();
        if(Double.isNaN(count)) {
            count = 0;
        }

        Sum agg = searchResponse.getAggregations().get("pageNum");
        double pageNum = agg.getValue();
        if(Double.isNaN(pageNum)) {
            pageNum = 0;
        }

        Sum aggtime = searchResponse.getAggregations().get("time");
        double time = aggtime.getValue();
        if(Double.isNaN(time)) {
            time = 0;
        }

        StringTerms ipTerms = (StringTerms) searchResponse.getAggregations().get("ipCount");
        double ip = ipTerms.getBuckets().size();
        if(Double.isNaN(ip)) {
            ip = 0;
        }

        LongTerms uvTerms = (LongTerms) searchResponse.getAggregations().get("uvCount");
        double uv = uvTerms.getBuckets().size();
        if(Double.isNaN(uv)) {
            uv = 0;
        }

        InternalDateHistogram shouluTerms = (InternalDateHistogram) searchResponse.getAggregations().get("day");
        int dayNum = shouluTerms.getBuckets().size();

        double avgTime =  time/count;
        avgTime = DoubleUtil.format(avgTime);
        double avgPage =  pageNum/count;
        avgPage = DoubleUtil.format(avgPage);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("pv", pageNum);
        map.put("uv", uv);
        map.put("ip", ip);
        map.put("avgTime", DoubleUtil.longToStringForTime(avgTime));
        map.put("avgPage", avgPage);
        if(hits.getTotalHits() > 0) {
            map.put("jump", DoubleUtil.format((double)jumphits.getTotalHits()/(double)hits.getTotalHits()*100));
        } else {
            map.put("jump", 0.0);
        }

        return map;
    }
}
