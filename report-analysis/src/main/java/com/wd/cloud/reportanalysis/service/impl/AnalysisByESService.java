package com.wd.cloud.reportanalysis.service.impl;

import com.wd.cloud.reportanalysis.entity.QueryCondition;
import com.wd.cloud.reportanalysis.repository.TransportRepository;
import com.wd.cloud.reportanalysis.service.AnalysisByESServiceI;
import com.wd.cloud.reportanalysis.util.AggregationBuilderUtil;
import com.wd.cloud.reportanalysis.util.QueryBuilderUtil;
import com.wd.cloud.reportanalysis.util.SearchRequestUtil;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Map.Entry;

@Service
public class AnalysisByESService implements AnalysisByESServiceI {

    @Autowired
    TransportRepository transportRepository;

    @Override
    public Map<String, Object> amount(List<QueryCondition> list, String filed, String type,Map<String,String> facetMap) {
        QueryBuilder queryBuilder = QueryBuilderUtil.convertQueryBuilder(list);
//        AbstractAggregationBuilder agg = AggregationBuilderUtil.convertQueryBuilder(filed);
        List<AbstractAggregationBuilder> aggs = AggregationBuilderUtil.buildFacetCondition1(facetMap);
        SearchResponse res = transportRepository.query(queryBuilder, null, aggs, type);
        return transform(res, filed,facetMap);
    }

    @Override
    public Map<String, Object> explain(String type) {
        SearchResponse res = transportRepository.query(null, null, null, "explain");
        for (SearchHit hit : res.getHits().getHits()) {
            Map<String, Object> source = hit.getSource();
            if (type.equals(source.get("type"))) {
                return source;
            }
        }
        return null;
    }

    public Map<String, Object> transform(SearchResponse searchResponse, String filed,Map<String,String> facetMap) {
        Map<String, Object> result = new HashMap<>();
        SearchHits searchHits = searchResponse.getHits();
        long total = searchHits.getTotalHits();
        double cites = 0;
        result.put("total", total);
        
//        for (Entry<String, String> facetFlag : facetMap.entrySet()) {
//        	String name = facetFlag.getKey();
//        	Terms yearTerms = (StringTerms) searchResponse.getAggregations().get(name);
//        	Iterator<org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket> iter = (Iterator<Bucket>) yearTerms.getBuckets().iterator();
//        	Map<String, Object> map = new LinkedHashMap<>();
//        	while (iter.hasNext()) {
//        		Bucket collegeBucket = iter.next();
//        		String key = collegeBucket.getKey().toString();
//        		long yearCount = collegeBucket.getDocCount();
//                double val = 0;
//                if (filed.equals("wosCites")) {
//                    InternalSum pvTerms = (InternalSum) collegeBucket.getAggregations().asMap().get("wosCites");
//                    double value = pvTerms.getValue();
//                    double avg = (double) Math.round(value / yearCount * 100) / 100;
//                    val = avg;
//                    cites += value;
//                } else if (filed.equals("wosCitesAll")) {
//                    InternalSum pvTerms = (InternalSum) collegeBucket.getAggregations().asMap().get("wosCites");
//                    double value = pvTerms.getValue();
//                    cites += value;
//                    val = value;
//                } 
//                else if(filed.equals("jcr_year")) {
//                	Terms jcrTerms = (Terms) collegeBucket.getAggregations().asMap().get("jcr");
//                	Iterator<org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket> jcrIter = (Iterator<Bucket>) jcrTerms.getBuckets().iterator();
//                	Map<String, Object> jcrMap = new LinkedHashMap<>();
//                	while (jcrIter.hasNext()) {
//                		Bucket jcrBucket = iter.next();
//                		String jcr = jcrBucket.getKey().toString();
//                		long jcrCount = collegeBucket.getDocCount();
//                		jcrMap.put(jcr, jcrCount);
//                	}
//                	map.put(key, jcrMap);
//                }
//                
//                else {
//                    val = yearCount;
//                }
//                if (StringUtils.isNotEmpty(key)) {
//                    map.put(key, val);
//                }
//        	}
//        }
        
        Terms yearTerms = (StringTerms) searchResponse.getAggregations().get(filed);
        Iterator<org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket> iter = (Iterator<Bucket>) yearTerms.getBuckets().iterator();
        Map<String, Object> map = new LinkedHashMap<>();
        while (iter.hasNext()) {
            Bucket collegeBucket = iter.next();
            String key = collegeBucket.getKey().toString();
            long yearCount = collegeBucket.getDocCount();
            double val = 0;
            if ("wosCites".equals(filed)) {
                InternalSum pvTerms = (InternalSum) collegeBucket.getAggregations().asMap().get("wosCites");
                double value = pvTerms.getValue();
                double avg = (double) Math.round(value / yearCount * 100) / 100;
                val = avg;
                cites += value;
            } else if ("wosCitesAll".equals(filed)) {
                InternalSum pvTerms = (InternalSum) collegeBucket.getAggregations().asMap().get("wosCites");
                double value = pvTerms.getValue();
                cites += value;
                val = value;
            }
            else if("jcr_year".equals(filed)) {
            	Terms jcrTerms = (Terms) collegeBucket.getAggregations().asMap().get("jcr");
            	Iterator<org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket> jcrIter = (Iterator<Bucket>) jcrTerms.getBuckets().iterator();
            	Map<String, Object> jcrMap = new LinkedHashMap<>();
            	while (jcrIter.hasNext()) {
            		Bucket jcrBucket = jcrIter.next();
            		String jcr = jcrBucket.getKey().toString();
            		long jcrCount = jcrBucket.getDocCount();
            		jcrMap.put(jcr, jcrCount);
            	}
            	map.put(key, jcrMap);
            }
            else {
                val = yearCount;
            }
            if (StringUtils.isNotEmpty(key) && !"jcr_year".equals(filed)) {
                map.put(key, val);
            }
        }
        result.put("list", map);
        if ("wosCites".equals(filed) || "wosCitesAll".equals(filed)) {
            result.put("cites", cites);
            double avgCites = (double) cites / total;
            avgCites = (double) Math.round(avgCites * 100) / 100;
            result.put("paper_cites", avgCites);
        }
        return result;
    }
    

}
