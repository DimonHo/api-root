package com.wd.cloud.reportanalysis.util;

import com.wd.cloud.reportanalysis.entity.FacetField;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.elasticsearch.search.aggregations.AggregationBuilders.terms;

/**
 * 构建聚类请求
 *
 * @author Administrator
 */
public class AggregationBuilderUtil {


    public static AbstractAggregationBuilder convertQueryBuilder(String filed) {
        if ("wosCitesAll".equals(filed) || "wosCites".equals(filed)) {        //总被引频次
            TermsAggregationBuilder termsBuilders = AggregationBuilders.terms(filed).field("year").size(Integer.MAX_VALUE).order(Terms.Order.term(true));
            AggregationBuilder termsBuilder = AggregationBuilders.sum("wosCites").field("wosCites");
            return termsBuilders.subAggregation(termsBuilder);
        }
        return terms(filed).field(filed).order(Terms.Order.term(true)).size(Integer.MAX_VALUE).shardSize(20).order(Terms.Order.term(true));
    }

    public static List<AbstractAggregationBuilder> buildFacetCondition1(Map<String, String> facetComponentNames) {
        List<AbstractAggregationBuilder> aggregationList = new ArrayList<AbstractAggregationBuilder>();
        for (Entry<String, String> facetFlag : facetComponentNames.entrySet()) {
            String name = facetFlag.getKey(), filed = facetFlag.getValue();
            AbstractAggregationBuilder abstractAggregationBuilder = terms(name).field(filed).order(Terms.Order.term(true)).size(Integer.MAX_VALUE).shardSize(20);
            if ("wosCitesAll".equals(name) || "wosCites".equals(name)) {
                AggregationBuilder termsBuilder = AggregationBuilders.sum("wosCites").field("wosCites");
                abstractAggregationBuilder.subAggregation(termsBuilder);
            }

            if ("jcr_year".equals(name)) {
                abstractAggregationBuilder = terms(name).field("year").order(Terms.Order.term(true)).size(Integer.MAX_VALUE).shardSize(20);
                AggregationBuilder termsBuilder = terms("jcr").field(filed).order(Terms.Order.term(true)).size(Integer.MAX_VALUE).shardSize(20);
                abstractAggregationBuilder.subAggregation(termsBuilder);
            }

            aggregationList.add(abstractAggregationBuilder);
        }
        return aggregationList;
    }

    public static List<AbstractAggregationBuilder> buildFacetCondition2(FacetField facetField) {
        List<AbstractAggregationBuilder> aggregationList = new ArrayList<AbstractAggregationBuilder>();

        String name = facetField.getName(), filed = facetField.getField();
        AbstractAggregationBuilder abstractAggregationBuilder = terms(name).field(filed).order(Terms.Order.term(true)).size(Integer.MAX_VALUE).shardSize(20);
        if ("wosCitesAll".equals(name) || "wosCites".equals(name)) {
            AggregationBuilder termsBuilder = AggregationBuilders.sum("wosCites").field("wosCites");
            abstractAggregationBuilder.subAggregation(termsBuilder);
        }

        if ("jcr_year".equals(name)) {
            abstractAggregationBuilder = terms(name).field("year").order(Terms.Order.term(true)).size(Integer.MAX_VALUE).shardSize(20);
            AggregationBuilder termsBuilder = terms("jcr").field(filed).order(Terms.Order.term(true)).size(Integer.MAX_VALUE).shardSize(20);
            abstractAggregationBuilder.subAggregation(termsBuilder);
        }

        aggregationList.add(abstractAggregationBuilder);

        return aggregationList;
    }

}
