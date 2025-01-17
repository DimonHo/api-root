package com.wd.cloud.bse.util;

import com.wd.cloud.bse.es.QueryBuildContext;
import com.wd.cloud.bse.vo.FacetField;
import com.wd.cloud.bse.vo.SearchCondition;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.util.automaton.RegExp;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.support.IncludeExclude;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 聚合查询组合器
 *
 * @author yangshuaifei
 */
@Component
public class FacetBuildUtil {

    @Autowired
    QueryBuildContext queryBuilderStrategyContext;

    /**
     * 聚合
     *
     * @return
     */
    public List<AggregationBuilder> buildFacets(SearchCondition searchCondition) {
        Map<String, List<FacetField>> facetsMap = queryBuilderStrategyContext.getFacets();
        List<FacetField> facets = searchCondition.getFacetFields();
        if (facets == null && searchCondition.getIsFacets() == 0) {
            String[] types = searchCondition.getTypes();
            facets = facetsMap.get("all");
            if (types != null && types.length == 1) {
                facets = facetsMap.get(types[0]);
            }
        }
        List<AggregationBuilder> facetInfoResult = new ArrayList<AggregationBuilder>();
        if (facets != null) {
            for (FacetField field : facets) {
                TermsAggregationBuilder tmpFacetBuilder = AggregationBuilders.terms(field.getName()).shardSize(Short.MAX_VALUE);
                tmpFacetBuilder.size(field.getSize());
                tmpFacetBuilder.field(field.getField());

                if ("journalTitle".equals(field.getName()) || "keyword".equals(field.getName())
                        || "author".equals(field.getName()) || "researchFields".equals(field.getName())
                        || "guider".equals(field.getName())) {        //解决聚合时聚合结果有空字符
                    RegExp exclude = new RegExp("");
                    IncludeExclude ie = new IncludeExclude(null, exclude);
                    tmpFacetBuilder.includeExclude(ie);
                }
                if (field.isOrder()) {
                    tmpFacetBuilder.order(Terms.Order.term(false));
                }
                if (field.isOrderByCount()) {
                    tmpFacetBuilder.order(Terms.Order.count(false));
                }
                AggregationBuilder aggBuilder = null;
                if (StringUtils.isNoneBlank(field.getNested())) {
                    aggBuilder = AggregationBuilders.nested(field.getName(), field.getNested());
                    aggBuilder.subAggregation(tmpFacetBuilder);
                } else {
                    aggBuilder = tmpFacetBuilder;
                }
                facetInfoResult.add(aggBuilder);
            }
        }

        return facetInfoResult;
    }

}
