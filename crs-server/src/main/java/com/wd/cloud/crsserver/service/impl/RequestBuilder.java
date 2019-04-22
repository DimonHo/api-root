package com.wd.cloud.crsserver.service.impl;

import com.weidu.commons.search.*;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchAction;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.Map.Entry;


/**
 * 构建查询请求
 * 更具查询条件，构建具体的查询请求
 *
 * @author Administrator
 */
public class RequestBuilder {


    public static SearchRequestBuilder initSearchRequestBuilder(BuilderStrategyContext builderStrategyContext,TransportClient transportClient, final SearchCondition searchCondition) {

        SearchRequestBuilder searchRequest = new SearchRequestBuilder(transportClient, SearchAction.INSTANCE);

        searchRequest.setIndices(searchCondition.getIndexName());

        if (searchCondition.getTypes() != null && searchCondition.getTypes().length > 0) {
            searchRequest.setTypes(searchCondition.getTypes());
        }
        searchRequest.setFrom(searchCondition.getFrom());
        searchRequest.setSize(searchCondition.getSize());

        List<String> highLightFields = new ArrayList<String>();
        searchRequest.setPreference(builderStrategyContext.getPreference());
        QueryBuilder query = buildQuery(builderStrategyContext, searchCondition, highLightFields);
        if (query != null) {
            // 如果是动态排序
            if (searchCondition.isFieldValueFactor()) {
                query = QueryBuilders.functionScoreQuery(query, ScoreFunctionBuilders.scriptFunction(searchCondition.getScript()));
            }
            searchRequest.setQuery(query);
        } else {
            if (searchCondition.isFieldValueFactor()) {
                query = QueryBuilders.functionScoreQuery(ScoreFunctionBuilders.scriptFunction(searchCondition.getScript()));
                searchRequest.setQuery(query);
            }
        }
        searchCondition.setHighLightFields(highLightFields);

        //只做统计
        if (searchCondition.isFacetOnly()) {
            searchRequest.setSize(0);
        } else {
            searchRequest.setSearchType(SearchType.QUERY_THEN_FETCH);
            buildSorts(searchCondition, searchRequest);
            buildHighLight(builderStrategyContext, searchCondition, searchRequest);
        }

        QueryBuilder filter = buildFilter(builderStrategyContext, searchCondition);
        if (filter != null) {
            searchRequest.setPostFilter(filter);
        }
        //不统计
        if (!searchCondition.isNoFacet()) {
            Collection<TermsAggregationBuilder> aggregationBuilders = buildFacets(builderStrategyContext, searchCondition);
            for (TermsAggregationBuilder aggregationBuilder : aggregationBuilders) {
                if (filter != null){
                    searchRequest.addAggregation(AggregationBuilders.filter(aggregationBuilder.getName(),filter).subAggregation(aggregationBuilder));
                }else{
                    searchRequest.addAggregation(aggregationBuilder);
                }
            }
        }
        // 忽略不存在的索引
        searchRequest.setIndicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        return searchRequest;
    }

    public static BoolQueryBuilder buildQuery(final BuilderStrategyContext builderContext, final List<List<SearchField>> queries, final List<String> highLightFields) {
        BoolQueryBuilder boolQueryBuilder = null, subBuilder = null;
        if (queries == null || queries.size() == 0) {
            return null;
        } else if (queries.size() == 1) {
            boolQueryBuilder = buildSubQuery(builderContext, queries.get(0), highLightFields);
        } else {
            boolQueryBuilder = QueryBuilders.boolQuery();
            for (List<SearchField> queryList : queries) {
                subBuilder = buildSubQuery(builderContext, queryList, highLightFields);
                if (subBuilder != null) {
                    boolQueryBuilder.must(subBuilder);
                }
            }
        }
        return boolQueryBuilder;
    }

    public static BoolQueryBuilder buildSubQuery(final BuilderStrategyContext builderContext, final List<SearchField> queries, final List<String> highLightFields) {
        if (queries == null || queries.size() == 0) {
            return null;
        }
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        int orSize = 0;

        for (SearchField condition : queries) {

            QueryBuilder queryBuilder = builderContext.getQueryBuilder(condition);
            if (null == queryBuilder) {
                continue;
            }

            switch (condition.getLogic().value()) {
                case 1: {
                    boolQueryBuilder.must(queryBuilder);
                    break;
                }
                case 2: {
                    orSize++;
                    boolQueryBuilder.should(queryBuilder);
                    break;
                }
                default: {
                    boolQueryBuilder.mustNot(queryBuilder);
                }
            }
            List<String> hFields = builderContext.getHighLightFields().get(condition.getFieldName());
            if (hFields != null && hFields.size() > 0) {
                for (String field : hFields) {
                    if (!highLightFields.contains(field)) {
                        highLightFields.add(field);
                    }
                }
            }
        }

        if (orSize > 1) {
            boolQueryBuilder.minimumNumberShouldMatch(1);
        }

        return boolQueryBuilder;
    }

    /**
     * 构建查询请求
     *
     * @param searchCondition
     * @return
     * @throws Exception
     */
    public static QueryBuilder buildQuery(final BuilderStrategyContext builderContext, final SearchCondition searchCondition, final List<String> highLightFields) throws RuntimeException {

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        List<SearchField> queryFields = searchCondition.getQueryFields();
        if (queryFields == null || queryFields.size() == 0) {
            return buildQuery(builderContext, searchCondition.getQueries(), highLightFields);
        } else {
            BoolQueryBuilder subQuery = buildSubQuery(builderContext, queryFields, highLightFields);
            BoolQueryBuilder subQuery2 = buildQuery(builderContext, searchCondition.getQueries(), highLightFields);
            if (subQuery == null) {
                return subQuery2;
            } else if (subQuery2 == null) {
                return subQuery;
            } else {
                return boolQueryBuilder.must(subQuery).must(subQuery2);
            }
        }
    }

    private static QueryBuilder buildFilter(final BuilderStrategyContext builderContext, List<SearchField> filterFields) {
        Map<String, List<SearchField>> fieldGroups = new HashMap<String, List<SearchField>>();
        List<SearchField> group;
        for (SearchField field : filterFields) {
            group = fieldGroups.get(field.getFieldName());
            if (group == null) {
                group = new ArrayList<SearchField>();
            }
            group.add(field);
            fieldGroups.put(field.getFieldName(), group);
        }
        BoolQueryBuilder boolFilterBuilder = QueryBuilders.boolQuery(), subBoolFilterBuilder;
        for (Entry<String, List<SearchField>> entry : fieldGroups.entrySet()) {
            subBoolFilterBuilder = QueryBuilders.boolQuery();
            for (SearchField field : entry.getValue()) {
                QueryBuilder builder = builderContext.getFilterBuilder(field);
                if (builder != null) {
                    switch (field.getLogic().value()) {
                        case 1:
                            subBoolFilterBuilder.must(builder);
                            break;
                        case 3:
                            subBoolFilterBuilder.mustNot(builder);
                            break;
                        default:
                            subBoolFilterBuilder.should(builder);
                    }
                }
            }
            boolFilterBuilder.must(subBoolFilterBuilder);
        }
        return boolFilterBuilder;
    }

    /**
     * 构建筛选请求
     *
     * @param searchCondition
     * @return
     * @throws Exception
     */
    protected static QueryBuilder buildFilter(final BuilderStrategyContext builderContext, final SearchCondition searchCondition) throws RuntimeException {

        List<List<SearchField>> filters = searchCondition.getFilters();

        if (filters != null && filters.size() > 0) {
            if (filters.size() == 1) {
                return buildFilter(builderContext, filters.get(0));
            } else {
                BoolQueryBuilder boolFilterBuilder = QueryBuilders.boolQuery();
                for (List<SearchField> filterFields : filters) {
                    boolFilterBuilder.must(buildFilter(builderContext, filterFields));
                }
                return boolFilterBuilder;
            }
        }
        return null;
    }

    /**
     * 构建聚类请求
     *
     * @param searchCondition
     * @return
     */
    protected static Collection<TermsAggregationBuilder> buildFacets(final BuilderStrategyContext builderContext, final SearchCondition searchCondition) {

        Collection<TermsAggregationBuilder> facetInfoResult = new ArrayList<TermsAggregationBuilder>();

        List<AggsField> facets = searchCondition.getAggsFields();

        if (facets == null || facets.size() == 0) {
            String groupName = searchCondition.getFacetGroup();
            if (StringUtils.isBlank(groupName)) {
                groupName = BuilderStrategyContext.DEFAULT_FACETGROUP_NAME;
            }
            facets = builderContext.getFacetFields(groupName);
        }

        if (facets != null) {
            for (AggsField field : facets) {
                TermsAggregationBuilder tmpFacetBuilder = AggregationBuilders.terms(field.getField()).shardSize(Short.MAX_VALUE);
                tmpFacetBuilder.size(field.getSize());
                tmpFacetBuilder.field(field.getField());
                tmpFacetBuilder.order(field.order());
                facetInfoResult.add(tmpFacetBuilder);
            }
        }
        return facetInfoResult;
    }

    /**
     * 高亮显示设置
     *
     * @param searchCondition
     * @return
     */
    protected static void buildHighLight(final BuilderStrategyContext builderContext, final SearchCondition searchCondition, final SearchRequestBuilder requestBuilder) {
        List<String> tmpHighlightFields = searchCondition.getHighLightFields();
        if (tmpHighlightFields != null && tmpHighlightFields.size() > 0) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            for (String fieldName : tmpHighlightFields) {
                HighlightBuilder.Field field = new HighlightBuilder.Field(fieldName);
                field.fragmentSize(builderContext.getHighLightFragmentSize());
                field.numOfFragments(builderContext.getNumOfFragments());
                field.postTags(builderContext.getHighLightPostTag());
                field.preTags(builderContext.getHighLightPreTag());
                highlightBuilder.field(field);
            }
            requestBuilder.highlighter(highlightBuilder);
        }

    }

    /**
     * 排序
     *
     * @param
     * @return
     */
    protected static void buildSorts(final SearchCondition searchCondition, final SearchRequestBuilder requestBuilder) {
        Map<String, Integer> tmpSorts = searchCondition.getSorts();
        if (tmpSorts != null) {
            for (Entry<String, Integer> entry : tmpSorts.entrySet()) {
                if (entry.getValue() == SortEnum.asc.value()) {
                    requestBuilder.addSort(entry.getKey(), SortOrder.ASC);
                }
                if (entry.getValue() == SortEnum.desc.value()) {
                    requestBuilder.addSort(entry.getKey(), SortOrder.DESC);
                }
            }
        }
    }


}
