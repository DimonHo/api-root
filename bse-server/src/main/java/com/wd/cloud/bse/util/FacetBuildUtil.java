package com.wd.cloud.bse.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;
import com.wd.cloud.bse.vo.FacetField;
import org.apache.lucene.util.automaton.RegExp;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.support.IncludeExclude;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class FacetBuildUtil {

    /**
     * 排序
     *
     * @return
     */
    public static List<AggregationBuilder> buildFacets() {
        List<FacetField> facets = new ArrayList<>();
        Iterator<Setting.Entry<String, String>> it = ConfigUtil.getFacesSettingIterator();
        while (it.hasNext()) {
            Entry<String, String> entry = it.next();
            String key = entry.getKey();
            String value = entry.getValue();

            String field = ConfigUtil.getStr(value + ".field");
            String size = ConfigUtil.getStr(value + ".size");
            String nested = ConfigUtil.getStr(value + ".nested");

            FacetField facetField = new FacetField(value, field, Integer.parseInt(size.trim()), false, false, nested);
            facets.add(facetField);
        }


        List<AggregationBuilder> facetInfoResult = new ArrayList<AggregationBuilder>();
        for (FacetField field : facets) {
            TermsAggregationBuilder tmpFacetBuilder = AggregationBuilders.terms(field.getName()).shardSize(Short.MAX_VALUE);
            tmpFacetBuilder.size(field.getSize());

            //解决聚合时聚合结果有空字符
            RegExp exclude = new RegExp("");
            IncludeExclude ie = new IncludeExclude(null, exclude);
            tmpFacetBuilder.includeExclude(ie);
            tmpFacetBuilder.field(field.getField());
            if (field.isOrder()) {
                tmpFacetBuilder.order(Terms.Order.term(false));
            }
            if (field.isOrderByCount()) {
                tmpFacetBuilder.order(Terms.Order.count(false));
            }
            AggregationBuilder aggBuilder = null;
            if (StrUtil.isNotBlank(field.getNested())) {
                aggBuilder = AggregationBuilders.nested(field.getName(), field.getNested());
                aggBuilder.subAggregation(tmpFacetBuilder);
            } else {
                aggBuilder = tmpFacetBuilder;
            }
//			QueryBuilder facetFilter = buildFacetFilter(searchCondition,field.getNested());
//			if(facetFilter != null){
//				AggregationBuilder builder= AggregationBuilders.filter(field.getName(), facetFilter);
//				builder.subAggregation(aggBuilder);
//				aggBuilder = builder;
//			}
            //tmpFacetBuilder.facetFilter(FilterBuilders.notFilter(FilterBuilders.termFilter("isDeleted", "1")));
            facetInfoResult.add(aggBuilder);


        }

        return facetInfoResult;
    }

}
