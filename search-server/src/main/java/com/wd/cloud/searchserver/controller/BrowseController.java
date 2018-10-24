package com.wd.cloud.searchserver.controller;

import com.wd.cloud.searchserver.util.*;
import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.search.MatchQuery;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.InternalDateHistogram;
import org.elasticsearch.search.aggregations.bucket.range.date.DateRangeAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.range.date.InternalDateRange;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.stats.Stats;
import org.elasticsearch.search.aggregations.metrics.stats.StatsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class BrowseController {

    @Autowired
    TransportClient transportClient;


    @RequestMapping("/indexInfo/{school}/{type}/{beginTime}/{endTime}/{day}")
    public Map<String, Object> indexInfo(@PathVariable String school,
                                         @PathVariable String type,
                                         @PathVariable String beginTime,
                                         @PathVariable String endTime,
                                         @PathVariable int day) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            SearchResponse searchResponse = null;
            RangeQueryBuilder filterBuilder = QueryBuilders.rangeQuery("lastTime");
            if (day != 5 && day != 4 && !"null".equals(day)) {
                beginTime = DateUtil.handleBeginTime(beginTime);
                endTime = DateUtil.handleEndTime(endTime);
                filterBuilder.from(beginTime + " 00:00:00").to(endTime);
            }

            BoolQueryBuilder q = QueryBuilders.boolQuery().must(filterBuilder);
            if (!StringUtils.isEmpty(school) && !"null".equals(school) && !"all".equals(school)) {
                QueryBuilder queryBuilder = QueryBuilders.termQuery("schoolFlag", school);
                q.must(queryBuilder);
            }
            if (type != null && !"null".equals(type) && !"-1".equals(type)) {
                QueryBuilder typeBuilder = QueryBuilders.termQuery("type", type);
                q.must(typeBuilder);
            }

            StatsAggregationBuilder countAggregation = AggregationBuilders
                    .stats("count").field("pageNum");

            SumAggregationBuilder aggregation = AggregationBuilders
                    .sum("pageNum").field("pageNum");

            SumAggregationBuilder timeAggregation = AggregationBuilders
                    .sum("time").field("time");

            TermsAggregationBuilder ipTermsBuilder = AggregationBuilders
                    .terms("ipCount").field("ip").size(Integer.MAX_VALUE);

            TermsAggregationBuilder uvTermsBuilder = AggregationBuilders
                    .terms("uvCount").field("memberId").size(Integer.MAX_VALUE);

            DateHistogramAggregationBuilder dayTermsBuilder = AggregationBuilders.dateHistogram("day")
                    .field("lastTime").format("yyyy-MM-dd").interval(60 * 60 * 24 * 1000).minDocCount(1);

            searchResponse = transportClient.prepareSearch(EsKey.BROWSE_INDEX).setTypes("periodical")
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
            SearchResponse jumpResponse = transportClient.prepareSearch(EsKey.BROWSE_INDEX).setTypes("periodical")
                    .setQuery(q.must(pageNumBuilder))
                    .addAggregation(countAggregation)
                    .execute()
                    .actionGet();
            SearchHits hits = searchResponse.getHits();
            SearchHits jumphits = jumpResponse.getHits();

            Stats counts = searchResponse.getAggregations().get("count");
            double count = counts.getCount();
            if (Double.isNaN(count)) {
                count = 0;
            }

            Sum agg = searchResponse.getAggregations().get("pageNum");
            double pageNum = agg.getValue();
            if (Double.isNaN(pageNum)) {
                pageNum = 0;
            }

            Sum aggtime = searchResponse.getAggregations().get("time");
            double time = aggtime.getValue();
            if (Double.isNaN(time)) {
                time = 0;
            }

            StringTerms ipTerms = (StringTerms) searchResponse.getAggregations().get("ipCount");
            double ip = ipTerms.getBuckets().size();
            if (Double.isNaN(ip)) {
                ip = 0;
            }

            LongTerms uvTerms = (LongTerms) searchResponse.getAggregations().get("uvCount");
            double uv = uvTerms.getBuckets().size();
            if (Double.isNaN(uv)) {
                uv = 0;
            }

            InternalDateHistogram shouluTerms = (InternalDateHistogram) searchResponse.getAggregations().get("day");
            int dayNum = shouluTerms.getBuckets().size();

            double avgTime = time / count;
            avgTime = DoubleUtil.format(avgTime);
            double avgPage = pageNum / count;
            avgPage = DoubleUtil.format(avgPage);

            map.put("pv", pageNum);
            map.put("uv", uv);
            map.put("ip", ip);
            map.put("avgTime", DoubleUtil.longToStringForTime(avgTime));
            map.put("avgPage", avgPage);
            if (hits.getTotalHits() > 0) {
                map.put("jump", DoubleUtil.format((double) jumphits.getTotalHits() / (double) hits.getTotalHits() * 100));
            } else {
                map.put("jump", 0.0);
            }
            if (day == 5) {
                map.put("avgTime", time);
                map.put("avgPage", pageNum);
            }
            if (day == 4 || day == 6) {
                map.put("allPv", pageNum);
                map.put("pv", DoubleUtil.format(pageNum / dayNum));
                map.put("uv", DoubleUtil.format(uv / dayNum));
                map.put("ip", DoubleUtil.format(ip / dayNum));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }


    /**
     * 浏览查询:获取用户浏览信息
     *
     * @param beginTime
     * @param endTime
     * @param school
     * @param day
     * @return
     */

    @RequestMapping("/flowQueryInfo/{beginTime}/{endTime}/{school}/{day}/{typesJson}/{type}")
    public Map<String, Object> flowQueryInfo(@PathVariable String beginTime,
                                             @PathVariable String endTime,
                                             @PathVariable String school,
                                             @PathVariable int day,
                                             @PathVariable String typesJson,
                                             @PathVariable String type) {
        String[] types = JsonUtil.json2Obj(typesJson, String[].class);
        Map<String, Object> json = new LinkedMap();
        SearchResponse searchResponse = null;
        BoolQueryBuilder queryBuilders = QueryBuilders.boolQuery();
        //构建查询范围
        if (!StringUtils.isEmpty(school) && !"all".equals(school)) {
            QueryBuilder queryBuilder = QueryBuilders.termQuery("schoolFlag", school);
            queryBuilders.must(queryBuilder);
        }
        if (type != null && !"-1".equals(type)) {
            QueryBuilder typeBuilder = QueryBuilders.termQuery("type", type);
            queryBuilders.must(typeBuilder);
        }
        RangeQueryBuilder filterBuilder = QueryBuilders.rangeQuery("lastTime")
                .from(DateUtil.handleBeginTime(beginTime) + " 00:00:00")
                .to(DateUtil.handleEndTime(endTime));
        queryBuilders.must(filterBuilder);
        //构建查询的时间聚合
        List<String> dayList = DateUtil.dayList(beginTime, endTime, day);
        json.put("dataList", dayList.subList(0, dayList.size() - 1));
        DateRangeAggregationBuilder timeTermsBuilder = AggregationBuilders.dateRange("lastTimeCount").field("beginTime");
        for (int i = 0; i < dayList.size(); i++) {
            if (day == 1 && beginTime.equals(endTime) && i < dayList.size() - 1) {
                List<String> hourList = new ArrayList<String>();
                for (int hour = 0; hour <= 23; hour++) {            //循环小时  当做map的key
                    if (hour < 10) {
                        hourList.add("0" + hour);
                    } else {
                        hourList.add(hour + "");
                    }
                }
                json.put("dataList", hourList);
                timeTermsBuilder.format("yyyy-MM-dd HH");
                timeTermsBuilder.addRange(dayList.get(i), dayList.get(i), dayList.get(i + 1));
            } else if (day == 2 || day == 3) {

                //json.put("dataList", dayList.subList(0, dayList.size()));
                timeTermsBuilder.format("yyyy-MM-dd");
                String aa = dayList.get(i).substring(0, 10);
                String aab = dayList.get(i).substring(11, 21);
                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
                Calendar cDay1 = Calendar.getInstance();
                try {
                    cDay1.setTime(sf.parse(aab));
                    cDay1.set(Calendar.DATE, cDay1.get(Calendar.DATE) + 1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                timeTermsBuilder.addRange(dayList.get(i), aa, sf.format(cDay1.getTime()));
                if (day == 3) {
                    dayList.add(i, dayList.remove(i).substring(0, 7));
                }
                json.put("dataList", dayList);
            } else if (i < dayList.size() - 1) {
                timeTermsBuilder.format("yyyy-MM-dd");
                timeTermsBuilder.addRange(dayList.get(i), dayList.get(i), dayList.get(i + 1));
            }
        }
        //构建具体数据聚合（sum、count）
        for (int i = 0; i < types.length; i++) {
            if ("浏览量（PV）".equals(types[i])) {
                SumAggregationBuilder termsBuilder = AggregationBuilders
                        .sum("pv").field("pageNum");
                timeTermsBuilder.subAggregation(termsBuilder);
            }
            if ("访客数（UV）".equals(types[i])) {
                TermsAggregationBuilder termsBuilder = AggregationBuilders
                        .terms("uv").field("memberId").size(Integer.MAX_VALUE);
                timeTermsBuilder.subAggregation(termsBuilder);
            }
            if ("IP数".equals(types[i])) {
                TermsAggregationBuilder termsBuilder = AggregationBuilders
                        .terms("ip").field("ip").size(Integer.MAX_VALUE);
                timeTermsBuilder.subAggregation(termsBuilder);
            }
            if ("平均访问时长".equals(types[i])) {
                SumAggregationBuilder termsBuilder = AggregationBuilders
                        .sum("time").field("time");
                timeTermsBuilder.subAggregation(termsBuilder);
            }
            if ("平均访问页数".equals(types[i])) {
                SumAggregationBuilder termsBuilder = AggregationBuilders
                        .sum("avgPage").field("pageNum");
                timeTermsBuilder.subAggregation(termsBuilder);
            }
            if ("跳出率".equals(types[i])) {
                TermsAggregationBuilder termsBuilder = AggregationBuilders.terms("jump").field("pageNum");
                timeTermsBuilder.subAggregation(termsBuilder);
            }
        }
        searchResponse = transportClient.prepareSearch(EsKey.BROWSE_INDEX).setTypes("periodical")
                .setQuery(queryBuilders)
                .addAggregation(timeTermsBuilder)
                .execute().actionGet();

        long total = searchResponse.getHits().getTotalHits();
        if (total == 0) {
            json.put("total", 0);
        }

        for (int i = 0; i < types.length; i++) {
            List<Double> list = new ArrayList<Double>();
            InternalDateRange shouluTerms = (InternalDateRange) searchResponse.getAggregations().get("lastTimeCount");
            Iterator<InternalDateRange.Bucket> shouluBucketIt = shouluTerms.getBuckets().iterator();
            while (shouluBucketIt.hasNext()) {
                InternalDateRange.Bucket collegeBucket = shouluBucketIt.next();
                String time = collegeBucket.getKey();
                long count = collegeBucket.getDocCount();
                double value = 0;
                if ("浏览量（PV）".equals(types[i])) {
                    Sum pvTerms = (Sum) collegeBucket.getAggregations().asMap().get("pv");
                    value = pvTerms.getValue();
                }
                if ("访客数（UV）".equals(types[i])) {
                    LongTerms uvTerms = (LongTerms) collegeBucket.getAggregations().asMap().get("uv");
                    value = uvTerms.getBuckets().size();
                }
                if ("IP数".equals(types[i])) {
                    StringTerms ipTerms = (StringTerms) collegeBucket.getAggregations().asMap().get("ip");
                    value = ipTerms.getBuckets().size();
                }
                if ("平均访问时长".equals(types[i])) {
                    Sum timeTerms = (Sum) collegeBucket.getAggregations().asMap().get("time");
                    value = timeTerms.getValue() / count;
                }
                if ("平均访问页数".equals(types[i])) {
                    Sum pvTerms = (Sum) collegeBucket.getAggregations().asMap().get("avgPage");
                    value = pvTerms.getValue() / count;
                }
                if ("跳出率".equals(types[i])) {
                    LongTerms jumpTerms = (LongTerms) collegeBucket.getAggregations().asMap().get("jump");
                    Iterator<org.elasticsearch.search.aggregations.bucket.terms.LongTerms.Bucket> jump = jumpTerms.getBuckets().iterator();
                    while (jump.hasNext()) {
                        org.elasticsearch.search.aggregations.bucket.terms.LongTerms.Bucket jumpBucket = jump.next();
                        Long jumpKey = (Long) jumpBucket.getKey();
                        if (jumpKey == 1) {
                            value = jumpBucket.getDocCount();
                            value = (value / count) * 100;
                            break;
                        }
                    }
                }
                list.add(DoubleUtil.format(value));
                json.put(types[i], list);
            }
        }
        return json;
    }


    /**
     * VisitHistory获取详细浏览记录历史
     *
     * @return
     */
    @RequestMapping("/visitHistoryInfo/")
    public List<Map<String, Object>> visitHistoryInfo(@RequestParam String school,
                                                      @RequestParam String val,
                                                      @RequestParam String ip,
                                                      @RequestParam String refererUrl,
                                                      @RequestParam String type,
                                                      @RequestParam Integer page,
                                                      @RequestParam Integer size) {
        SearchResponse searchResponse = null;
        SystemContext.setOffset(page * size);
        SystemContext.setPageSize(size);
        BoolQueryBuilder queryBuilders = QueryBuilders.boolQuery();
        //构建查询请求
        SearchRequestBuilder requestBuilder = transportClient.prepareSearch(EsKey.BROWSE_INDEX).setTypes(EsKey.BROWSE_TYPE);
        boolean isQuery = false;
        if (StringUtils.isNotEmpty(school) && !"null".equals(school) && !"all".equals(school)) {
            QueryBuilder builder = QueryBuilders.termQuery("schoolFlag", school);
            queryBuilders.must(builder);
            isQuery = true;
        }
        //当前时间
        RangeQueryBuilder filterBuilder = QueryBuilders.rangeQuery("lastTime").to(DateUtil.handleEndTime(null));
        queryBuilders.must(filterBuilder);

        if (type != null && !"-1".equals(type)) {
            QueryBuilder typeBuilder = QueryBuilders.termQuery("type", type);
            queryBuilders.must(typeBuilder);
            isQuery = true;
        }
        if (StringUtils.isNotEmpty(ip) && !"null".equals(ip)) {
            QueryBuilder builder = QueryBuilders.termQuery("ip", ip);
            queryBuilders.must(builder);
            isQuery = true;
        }
        if (StringUtils.isNotEmpty(refererUrl) && !"null".equals(refererUrl)) {
            QueryBuilder builder = QueryBuilders.matchQuery("chickPageList.url", refererUrl)
                    .type(MatchQuery.Type.PHRASE).slop(10000);
            queryBuilders.must(builder);
            isQuery = true;
        }
        if (StringUtils.isNotEmpty(val) && !"null".equals(val)) {
            QueryBuilder builder = QueryBuilders.matchQuery("chickPageList.keyWord", val)
                    .type(MatchQuery.Type.PHRASE).slop(10000);
            queryBuilders.must(builder);
            isQuery = true;
        }
        //根据时间倒序排序
        SortBuilder sort = SortBuilders.fieldSort("beginTime").order(SortOrder.DESC);
        requestBuilder.addSort(sort);
        //if(isQuery) { //如果有查询条件
        requestBuilder.setQuery(queryBuilders);
        //}
        int offset = SystemContext.getOffset();
        searchResponse = requestBuilder.setFrom(offset).setSize(20).execute().actionGet();
        SearchHits hits = searchResponse.getHits();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if (hits.getTotalHits() > 0) {
            for (SearchHit hit : hits) {
                Map<String, Object> map = hit.getSource();
                map.put("count", hits.getTotalHits());
                list.add(map);
            }
        } else {
            System.out.println("搜到0条结果");
        }
        return list;
    }

    /**
     * 导出详细浏览记录历史(scrollId分页)
     *
     * @return
     */
    @RequestMapping("/getvisitHistoryInfoScrollId/{school}/{beginTime}/{endTime}/{type}")
    public SearchResponse getvisitHistoryInfoScrollId(@PathVariable String school,
                                                      @PathVariable String beginTime,
                                                      @PathVariable String endTime,
                                                      @PathVariable String type) {
        SearchResponse searchResponse = null;
        try {
            RangeQueryBuilder filterBuilder = QueryBuilders.rangeQuery("lastTime").from(beginTime + " 00:00:00").to(endTime + " 23:59:59");
            BoolQueryBuilder q = QueryBuilders.boolQuery();
            q.must(filterBuilder);
            if (StringUtils.isNotEmpty(school) && !"null".equals(school) && !"all".equals(school)) {
                QueryBuilder schoolQuery = QueryBuilders.termQuery("schoolFlag", school);
                q.must(schoolQuery);
            }
            if (Integer.parseInt(type) != -1) {
                QueryBuilder typeQuery = QueryBuilders.termQuery("type", type);
                q.must(typeQuery);
            }
            SearchRequestBuilder builder = transportClient.prepareSearch(EsKey.BROWSE_INDEX).setTypes(EsKey.BROWSE_TYPE)
                    .setQuery(q)
                    .setScroll(TimeValue.timeValueMinutes(80)).setSize(200);
            searchResponse = builder.execute().actionGet();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return searchResponse;
    }

    @RequestMapping("/getvisitHistoryInfo/{scrollId}")
    public SearchHits getvisitHistoryInfo(@PathVariable String scrollId) {
        SearchResponse response = transportClient.prepareSearchScroll(scrollId)
                .setScroll(TimeValue.timeValueMinutes(80))
                .execute().actionGet();
        //获取查询结果集
        SearchHits searchHits = response.getHits();
        return searchHits;
    }

    /**
     * 计算VisitHistory当天访问频次和上一次访问时间
     *
     * @param memberId
     * @param time
     * @param data
     */
    @RequestMapping("/visitHistoryInfoById/{memberId}/{time}/data")
    public void visitHistoryInfoById(@PathVariable int memberId,
                                     @PathVariable String time,
                                     Map<String, Object> data) {
        SearchResponse searchResponse = null;
        BoolQueryBuilder queryBuilders = QueryBuilders.boolQuery();
        //构建查询请求
        QueryBuilder builder = QueryBuilders.termQuery("memberId", memberId);
        queryBuilders.must(builder);

        RangeQueryBuilder filterBuilder = QueryBuilders.rangeQuery("beginTime").to(time);
        queryBuilders.must(filterBuilder);

        //根据时间倒序排序
        SortBuilder sort = SortBuilders.fieldSort("beginTime").order(SortOrder.DESC);

        searchResponse = transportClient.prepareSearch(EsKey.BROWSE_INDEX).setTypes(EsKey.BROWSE_TYPE)
                .addSort(sort)
                .setQuery(queryBuilders)
                .execute().actionGet();

        SearchHits hits = searchResponse.getHits();
        int rate = 0;
        for (SearchHit hit : hits) {
            Map<String, Object> map = hit.getSource();
            String beginTime = (String) hit.getSource().get("beginTime");
            if (!data.containsKey("lastVisitTime") && rate != 0 && !"null".equals(rate)) {
                data.put("lastVisitTime", beginTime);
            }
            if (beginTime.substring(0, 10).equals(time.substring(0, 10))) {
                rate++;
            }

        }
        data.put("rate", rate);
    }

    /**
     * 如果是comefrom（学校详细，必须有学校）filed：refererUrl
     * 如果是comefrom（学校列表）filed：schoolFlag
     * findRegion地域分布table表（必须有学校）filed：schoolProvince
     * @param school
     * @param beginTime
     * @param endTime
     * @param filed
     * @return
     */
    @RequestMapping("/visitLan/{typesJson}/")
    public List<Map<String, Object>> visitLan(@PathVariable String typesJson,
                                              @RequestParam String school,
                                              @RequestParam String beginTime,
                                              @RequestParam String endTime,
                                              @RequestParam String filed,
                                              @RequestParam int offset,
                                              @RequestParam int size,
                                              @RequestParam int memberType,
                                              @RequestParam String sort,
                                              @RequestParam String dataType) {
        String[] types = JsonUtil.json2Obj(typesJson, String[].class);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            SearchResponse searchResponse = null;
            BoolQueryBuilder queryBuilders = QueryBuilders.boolQuery();
            //构建查询范围
            if (!StringUtils.isEmpty(school) && !"null".equals(school) && !"all".equals(school) /*&& !filed.equals("schoolFlag")*/ && !"schoolFlagRegion".equals(filed)) {
                QueryBuilder queryBuilder = QueryBuilders.termQuery("schoolFlag", school);
                queryBuilders.must(queryBuilder);
            } else if ("schoolFlagRegion".equals(filed)) {
                filed = "schoolFlag";
                if ("其他".equals(school)) {
                    school = "";
                }
                QueryBuilder queryBuilder = QueryBuilders.termQuery("schoolProvince", school);
                queryBuilders.must(queryBuilder);
            }
            if (memberType != -1) {
                QueryBuilder queryBuilder = QueryBuilders.termQuery("memberType", memberType);
                queryBuilders.must(queryBuilder);
            }
            if (dataType != null && !"null".equals(dataType) && !"-1".equals(dataType)) {//数据类型（原始，添加）
                QueryBuilder typeBuilder = QueryBuilders.termQuery("type", dataType);
                queryBuilders.must(typeBuilder);
            }
            beginTime = DateUtil.handleBeginTime(beginTime);
            endTime = DateUtil.handleEndTime(endTime);
            RangeQueryBuilder filterBuilder = QueryBuilders.rangeQuery("lastTime").from(beginTime + " 00:00:00").to(endTime);


            queryBuilders.must(filterBuilder);
            TermsAggregationBuilder termsBuilders = AggregationBuilders.terms("agg").field(filed).size(Integer.MAX_VALUE);
            //构建具体数据聚合（sum、count）
            for (int i = 0; i < types.length; i++) {
                boolean sortBool = true;
                if (sort != null && sort.contains("down")) {//升
                    sortBool = false;
                }
                String type = types[i];
                if ("浏览量（PV）".equals(type)) {
                    SumAggregationBuilder termsBuilder = AggregationBuilders
                            .sum("pv").field("pageNum");
                    termsBuilders.subAggregation(termsBuilder);
                }
                if ("访客数（UV）".equals(type)) {
                    TermsAggregationBuilder termsBuilder = AggregationBuilders
                            .terms("uv").field("memberId").size(Integer.MAX_VALUE);
                    termsBuilders.subAggregation(termsBuilder);
                    if ("memberType".equals(filed)) {
                        TermsAggregationBuilder registerTermsBuilder = AggregationBuilders
                                .terms("register").field("register").size(Integer.MAX_VALUE);
                        termsBuilder.subAggregation(registerTermsBuilder);
                    }
                }
                if ("IP数".equals(type)) {
                    TermsAggregationBuilder termsBuilder = AggregationBuilders
                            .terms("ip").field("ip").size(Integer.MAX_VALUE);
                    termsBuilders.subAggregation(termsBuilder);
                }
                if ("平均访问时长".equals(type)) {
                    SumAggregationBuilder termsBuilder = AggregationBuilders
                            .sum("avgTime").field("time");
                    termsBuilders.subAggregation(termsBuilder);
                }
                if ("平均访问页数".equals(type)) {
                    SumAggregationBuilder termsBuilder = AggregationBuilders
                            .sum("avgPage").field("pageNum");
                    termsBuilders.subAggregation(termsBuilder);
                }
                if ("跳出率".equals(type)) {
                    TermsAggregationBuilder termsBuilder = AggregationBuilders
                            .terms("jump").field("pageNum").size(Integer.MAX_VALUE);
                    termsBuilders.subAggregation(termsBuilder);
                }
            }
            searchResponse = transportClient.prepareSearch(EsKey.BROWSE_INDEX).setTypes(EsKey.BROWSE_TYPE)
                    .setQuery(queryBuilders)
                    .addAggregation(termsBuilders)
                    .execute().actionGet();
            Terms shouluTerms = null;
            if ("memberType".equals(filed)) {  //新老访客记录
                shouluTerms = (LongTerms) searchResponse.getAggregations().get("agg");
            } else {
                shouluTerms = (StringTerms) searchResponse.getAggregations().get("agg");
            }
            Iterator<? extends org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket> shouluBucketIt = shouluTerms.getBuckets().iterator();
            while (shouluBucketIt.hasNext()) {
                Map<String, Object> json = new LinkedMap();
                org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket collegeBucket = shouluBucketIt.next();
                Object key = collegeBucket.getKey();
                long count = collegeBucket.getDocCount();
                json.put(filed, key);
                if (offset != -1) {
                    json.put("count", shouluTerms.getBuckets().size());
                }
                for (int i = 0; i < types.length; i++) {
                    double value = 0;
                    String type = types[i];
                    if ("浏览量（PV）".equals(type)) {
                        type = "pv";
                        Sum pvTerms = (Sum) collegeBucket.getAggregations().asMap().get("pv");
                        value = pvTerms.getValue();
                    }
                    if ("访客数（UV）".equals(type)) {
                        type = "uv";
                        LongTerms uvTerms = (LongTerms) collegeBucket.getAggregations().asMap().get("uv");
                        value = uvTerms.getBuckets().size();
                        if ("memberType".equals(filed)) {  //新老访客记录
                            int registerNum = 0;
                            Iterator<org.elasticsearch.search.aggregations.bucket.terms.LongTerms.Bucket> uvBucketIt = uvTerms.getBuckets().iterator();
                            while (uvBucketIt.hasNext()) {
                                org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket uvBucket = uvBucketIt.next();
                                LongTerms registerTerms = (LongTerms) uvBucket.getAggregations().asMap().get("register");
                                Iterator<org.elasticsearch.search.aggregations.bucket.terms.LongTerms.Bucket> register = registerTerms.getBuckets().iterator();
                                while (register.hasNext()) {
                                    org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket registerBucket = register.next();
                                    Long registerKey = (Long) registerBucket.getKey();
                                    if (registerKey == 1) {
                                        registerNum++;
                                    }
                                }
                            }
                            json.put("register", registerNum);
                        }
                    }
                    if ("IP数".equals(type)) {
                        type = "ip";
                        StringTerms ipTerms = (StringTerms) collegeBucket.getAggregations().asMap().get("ip");
                        value = ipTerms.getBuckets().size();
                    }
                    if ("平均访问时长".equals(type)) {
                        type = "avgTime";
                        Sum timeTerms = (Sum) collegeBucket.getAggregations().asMap().get("avgTime");
                        value = timeTerms.getValue() / count;
                    }
                    if ("平均访问页数".equals(type)) {
                        type = "avgPage";
                        Sum pvTerms = (Sum) collegeBucket.getAggregations().asMap().get("avgPage");
                        value = pvTerms.getValue() / count;
                    }
                    if ("跳出率".equals(type)) {
                        type = "jump";
                        LongTerms jumpTerms = (LongTerms) collegeBucket.getAggregations().asMap().get("jump");
                        Iterator<org.elasticsearch.search.aggregations.bucket.terms.LongTerms.Bucket> jump = jumpTerms.getBuckets().iterator();
                        while (jump.hasNext()) {
                            org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket jumpBucket = jump.next();
                            Long junpKey = (Long) jumpBucket.getKey();
                            if (junpKey == 1) {
                                value = jumpBucket.getDocCount();
                                value = (value / count) * 100;
                                break;
                            }
                        }
                    }
                    json.put(type, DoubleUtil.format(value));
                    if ("avgTime".equals(type)) {
                        json.put(type, DoubleUtil.longToStringForTime(value));
                    }
                }
                list.add(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * 留存用户计算
     *
     * @param beginTime
     * @param endTime
     * @param school
     * @param day
     * @param memberType
     * @return
     */
    @RequestMapping("/keep/{beginTime}/{endTime}/{school}/{day}/{memberType}/{type}")
    public Map<String, Map<String, Long>> keep(@PathVariable String beginTime,
                                               @PathVariable String endTime,
                                               @PathVariable String school,
                                               @PathVariable int day,
                                               @PathVariable int memberType,
                                               @PathVariable String type) {

        SearchResponse searchResponse = null;
        BoolQueryBuilder queryBuilders = QueryBuilders.boolQuery();
        //构建查询范围
        if (!StringUtils.isEmpty(school) && !"null".equals(school) && !"all".equals(school)) {
            QueryBuilder queryBuilder = QueryBuilders.termQuery("schoolFlag", school);
            queryBuilders.must(queryBuilder);
        }
        if (type != null && !"null".equals(type) && !"-1".equals(type)) {//数据类型（原始，添加）
            QueryBuilder typeBuilder = QueryBuilders.termQuery("type", type);
            queryBuilders.must(typeBuilder);
        }
        QueryBuilder queryBuilder = QueryBuilders.termQuery("memberType", memberType);
        queryBuilders.must(queryBuilder);

        beginTime = DateUtil.handleBeginTime(beginTime);
        endTime = DateUtil.handleEndTime(endTime);
        RangeQueryBuilder filterBuilder = QueryBuilders.rangeQuery("lastTime").from(beginTime + " 00:00:00").to(endTime);
        queryBuilders.must(filterBuilder);

        //构建查询的时间聚合
        List<String> dayList = null;
        if (beginTime.equals(endTime)) {
            endTime = endTime + " ";
        }
        dayList = DateUtil.dayList(beginTime, endTime, day);
        DateRangeAggregationBuilder timeTermsBuilder = AggregationBuilders.dateRange("lastTimeCount").field("lastTime");

        TermsAggregationBuilder termsBuilder = AggregationBuilders
                .terms("uv").field("memberId").size(Integer.MAX_VALUE);
        timeTermsBuilder.subAggregation(termsBuilder);

        for (int i = 0; i < dayList.size(); i++) {
            if (day == 1 && i < dayList.size() - 1) {
                timeTermsBuilder.format("yyyy-MM-dd");
                timeTermsBuilder.addRange(dayList.get(i), dayList.get(i), dayList.get(i + 1));
            } else if (day == 2 || day == 3) {
                timeTermsBuilder.format("yyyy-MM-dd");
                String aa = dayList.get(i).substring(0, 10);
                String aab = dayList.get(i).substring(11, 21);
                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
                Calendar cDay1 = Calendar.getInstance();
                try {
                    cDay1.setTime(sf.parse(aab));
                    cDay1.set(Calendar.DATE, cDay1.get(Calendar.DATE) + 1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                timeTermsBuilder.addRange(dayList.get(i), aa, sf.format(cDay1.getTime()));
            }
        }

        searchResponse = transportClient.prepareSearch(EsKey.BROWSE_INDEX).setTypes(EsKey.BROWSE_TYPE)
                .setQuery(queryBuilders)
                .addAggregation(timeTermsBuilder)
                .execute().actionGet();
        InternalDateRange shouluTerms = (InternalDateRange) searchResponse.getAggregations().get("lastTimeCount");
        Iterator<InternalDateRange.Bucket> shouluBucketIt = shouluTerms.getBuckets().iterator();
        Map<String, Map<String, Long>> timeMap = new LinkedMap();
        while (shouluBucketIt.hasNext()) {
            Map<String, Long> memberMap = new HashMap<String, Long>();
            InternalDateRange.Bucket collegeBucket = shouluBucketIt.next();
            String time = collegeBucket.getKey();

            LongTerms uvTerms = (LongTerms) collegeBucket.getAggregations().asMap().get("uv");
            Iterator<org.elasticsearch.search.aggregations.bucket.terms.LongTerms.Bucket> uv = uvTerms.getBuckets().iterator();
            long count = uvTerms.getBuckets().size();
            memberMap.put("count", count);
            while (uv.hasNext()) {
                org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket uvBucket = uv.next();
                memberMap.put((Long) uvBucket.getKey() + "", uvBucket.getDocCount());
            }
            timeMap.put(time, memberMap);
            int value = uvTerms.getBuckets().size();
        }
        return timeMap;
    }

    /**
     * 访问页面
     */
    @RequestMapping("/visitPage/")
    public List<Map<String, Object>> visitPage(@RequestParam String beginTime,
                                               @RequestParam String endTime,
                                               @RequestParam String school,
                                               @RequestParam String type) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            SearchResponse searchResponse = null;
            StatsAggregationBuilder countAggregation = AggregationBuilders
                    .stats("count").field("pageNum");
            TermsAggregationBuilder uvTermsBuilder = AggregationBuilders
                    .terms("uvCount").field("memberId").size(Integer.MAX_VALUE);
            double allCount = 0;
            DecimalFormat df = new DecimalFormat("0.0%");
            for (int i = 0; i < CommKey.VISIT_PAGE.size(); i++) {
                BoolQueryBuilder queryBuilders = QueryBuilders.boolQuery();
                //构建查询范围
                if (!StringUtils.isEmpty(school) && !"null".equals(school) && !"all".equals(school)) {
                    QueryBuilder queryBuilder = QueryBuilders.termQuery("schoolFlag", school);
                    queryBuilders.must(queryBuilder);
                }
                if (type != null && !"null".equals(type) && !"-1".equals(type)) {//数据类型（原始，添加）
                    QueryBuilder typeBuilder = QueryBuilders.termQuery("type", type);
                    queryBuilders.must(typeBuilder);
                }
                beginTime = DateUtil.handleBeginTime(beginTime);
                endTime = DateUtil.handleEndTime(endTime);
                RangeQueryBuilder filterBuilder = QueryBuilders.rangeQuery("lastTime").from(beginTime).to(endTime);
                queryBuilders.must(filterBuilder);
                Map<String, Object> result = new HashMap<String, Object>();
                int start = 0;
                if (i != 0) {
                    start = CommKey.VISIT_PAGE.get(i - 1) + 1;
                }
                int end = CommKey.VISIT_PAGE.get(i);
                if (i == 0) {
                    searchResponse = transportClient.prepareSearch(EsKey.BROWSE_INDEX).setTypes(EsKey.BROWSE_TYPE)
                            .setQuery(queryBuilders)
                            .addAggregation(countAggregation)
                            .addAggregation(uvTermsBuilder)
                            .execute().actionGet();
                    Stats counts = searchResponse.getAggregations().get("count");
                    allCount = counts.getCount();
                    if (Double.isNaN(allCount)) {
                        allCount = 0;
                    }
                }
                RangeQueryBuilder queryBuilder = QueryBuilders.rangeQuery("pageNum").from(start).to(end);
                queryBuilders.must(queryBuilder);
                searchResponse = transportClient.prepareSearch(EsKey.BROWSE_INDEX).setTypes(EsKey.BROWSE_TYPE)
                        .setQuery(queryBuilders)
                        .addAggregation(countAggregation)
                        .addAggregation(uvTermsBuilder)
                        .execute().actionGet();
                Stats counts = searchResponse.getAggregations().get("count");
                double count = counts.getCount();
                if (Double.isNaN(count)) {
                    count = 0;
                }
                LongTerms uvTerms = (LongTerms) searchResponse.getAggregations().get("uvCount");
                double uv = uvTerms.getBuckets().size();
                if (Double.isNaN(uv)) {
                    uv = 0;
                }
                result.put("page", start + "-" + end);
                result.put("uv", uv);
                result.put("count", count);
                if (count == 0 || allCount == 0) {
                    result.put("percent", "0.0%");
                } else {
                    result.put("percent", df.format(count / allCount));
                }
                list.add(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return list;
    }

    @RequestMapping("/insert/{data}")
    public void insert(@PathVariable Map<String, Object> data) throws Exception {
        XContentBuilder contentBuilder = XContentFactory.jsonBuilder().startObject();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            contentBuilder = contentBuilder.field(entry.getKey(), entry.getValue());
        }
        contentBuilder = contentBuilder.endObject();
        try {
            transportClient.prepareIndex(EsKey.BROWSE_INDEX, EsKey.BROWSE_TYPE)
                    .setOpType(DocWriteRequest.OpType.INDEX)
                    .setSource(contentBuilder).get();
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    @RequestMapping("/getDoc/{id}")
    public Map<String, Object> getDoc(@PathVariable String id){
        GetResponse resp=transportClient.prepareGet().setIndex(EsKey.DEFAULT_INDEX).setId(id).execute().actionGet();
        Map<String, Object> doc=resp.getSource();
        doc.put("_id", id);
        return doc;
    }

    @RequestMapping("/getSuggest/{value}/{size}")
    public List<String> getSuggest(@PathVariable String value,
                                   @PathVariable int size) {
        assert size>0;
        SuggestBuilder suggestionsBuilder = new  SuggestBuilder();
        suggestionsBuilder.addSuggestion("complete", SuggestBuilders.completionSuggestion("suggest").text(value).size(size));
        SearchResponse resp  = transportClient.prepareSearch(EsKey.DEFAULT_INDEX).suggest(suggestionsBuilder).execute().actionGet();
        List<String> result = new ArrayList<String>();
        List<? extends Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option>>  entries = resp.getSuggest().getSuggestion("complete").getEntries();
        for (Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option> e : entries) {
            Iterator<? extends Suggest.Suggestion.Entry.Option> ite  = e.getOptions().iterator();
            while(ite.hasNext()){
                Suggest.Suggestion.Entry.Option o = ite.next();
                result.add(o.getText().toString());
            }
        }
        return result;
    }



}
