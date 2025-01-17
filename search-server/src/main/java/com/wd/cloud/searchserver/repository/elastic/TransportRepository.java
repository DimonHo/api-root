package com.wd.cloud.searchserver.repository.elastic;

import cn.hutool.core.lang.Console;
import com.wd.cloud.commons.enums.StatusEnum;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.searchserver.util.SystemContext;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.engine.DocumentMissingException;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * TransportRepository class
 *
 * @author hezhigang
 * @date 2018/04/08
 */
@Repository("transportRepository")
public class TransportRepository implements ElasticRepository {

    @Autowired
    TransportClient transportClient;

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> convertDocList(
            SearchResponse searchResponse) {
        List<Map<String, Object>> datas = null;
        SearchHits searchHits = searchResponse.getHits();
        SearchHit[] hitArr = searchHits.getHits();
        if (null != hitArr && hitArr.length > 0) {
            datas = new ArrayList<Map<String, Object>>(hitArr.length);
            for (SearchHit hit : hitArr) {
                Map<String, Object> source = hit.getSource();
                source.put("_id", hit.getId());
                // 获取高亮值
                Map<String, HighlightField> hightLightMap = hit
                        .getHighlightFields();
                Set<String> keySet = hightLightMap.keySet();
                String value = null;
                for (String highlightField : keySet) {
                    HighlightField highlightValue = hightLightMap
                            .get(highlightField);
                    value = highlightValue.fragments()[0].string();
                    // 将高亮值也放入source中，高亮值的字段名必须符合命名规范(原字段名_highlight)
                    source.put(highlightField, value);
                }

                datas.add(source);
            }
        } else {
            datas = Collections.EMPTY_LIST;
        }

        return datas;
    }

    @Override
    public ResponseModel createIndex(String index) {
        return createIndex(index, null, null, null);
    }

    @Override
    public ResponseModel createIndex(String index, Settings settings) {
        return createIndex(index, null, settings, null);
    }

    @Override
    public ResponseModel createIndex(String index, String type, Map<String, Object> mapping) {
        return createIndex(index, type, null, mapping);
    }

    @Override
    public ResponseModel<CreateIndexResponse> createIndex(String index, String type, Settings settings, Map<String, Object> mapping) {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest();
        createIndexRequest.index(index);
        if (settings != null) {
            createIndexRequest.settings(settings);
        }
        if (mapping != null) {
            createIndexRequest.mapping(type, mapping);
        }
        CreateIndexResponse response = transportClient.admin().indices().create(createIndexRequest).actionGet(2000);
        return ResponseModel.ok().setBody(response);
    }

    @Override
    public ResponseModel<SearchResponse> matchAll(String index, String type) {
        SearchResponse response = transportClient.prepareSearch(index).setTypes(type).get();
        return ResponseModel.ok().setBody(response);
    }

    @Override
    public ResponseModel<GetResponse> getDocById(String index, String type, String id) {
        GetResponse response = transportClient.prepareGet(index, type, id).get();
        return ResponseModel.ok().setBody(response);
    }

    @Override
    public ResponseModel updateFieldById(String index, String type, String id, Map<String, Object> fieldMap) {
        try {
            RestStatus response = transportClient.prepareUpdate(index, type, id).setDoc(fieldMap).get().status();
            return ResponseModel.ok().setBody(response);
        } catch (DocumentMissingException e) {
            Console.log("Document：{}未找到", id);
            return ResponseModel.fail(StatusEnum.NOT_FOUND);
        }
    }

    @Override
    public ResponseModel<RestStatus> update(UpdateRequest updateRequest) {
        ResponseModel responseModel = new ResponseModel();
        try {
            RestStatus restStatus = transportClient.update(updateRequest).get().status();
            responseModel = ResponseModel.ok().setBody(restStatus);
        } catch (InterruptedException e) {
            e.printStackTrace();
            // responseModel = ResponseModel.error(500, "InterruptedException");
            responseModel = ResponseModel.fail(e);
        } catch (ExecutionException e) {
            e.printStackTrace();
            //responseModel = ResponseModel.error(500, "ExecutionException");
            responseModel = ResponseModel.fail(e);
        }
        return responseModel;
    }

    @Override
    public boolean isExistsById(String index, String type, String id) {
        return transportClient.prepareGet(index, type, id).get().isExists();
    }

    @Override
    public ResponseModel<SearchResponse> scrollAll(String index, String type) {
        return scrollAll(index, type, 1000 * 10, 10);
    }

    @Override
    public ResponseModel<SearchResponse> scrollAll(String index, String type, long timeValue) {
        return scrollAll(index, type, timeValue, 10);
    }

    @Override
    public ResponseModel<SearchResponse> scrollAll(String index, String type, int batchSize) {
        return scrollAll(index, type, 1000 * 60, batchSize);
    }

    @Override
    public ResponseModel<SearchResponse> scrollAll(String index, String type, long timeValue, int batchSize) {
        SearchResponse response = transportClient.prepareSearch(index).setTypes(type)
                .addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC)
                .setScroll(TimeValue.timeValueMillis(timeValue))
                .setSize(batchSize)
                .get();
        return ResponseModel.ok().setBody(response);
    }

    @Override
    public ResponseModel<SearchResponse> scrollByQuery(String index, String type, QueryBuilder queryBuilder) {
        SearchResponse response = transportClient.prepareSearch(index).setTypes(type)
                .setQuery(queryBuilder)
                .setScroll(TimeValue.timeValueMillis(1000 * 60))
                .get();
        return ResponseModel.ok().setBody(response);
    }

    @Override
    public ResponseModel<SearchResponse> scrollAllReFields(String index, String type, String[] returnFields) {
        return scrollByQueryReFields(index, type, QueryBuilders.matchAllQuery(), returnFields);
    }

    @Override
    public ResponseModel<SearchResponse> scrollByQueryReFields(String index, String type, QueryBuilder queryBuilder, String[] returnFields) {
        return scrollByQueryReFields(index, type, queryBuilder, returnFields, 10);
    }

    @Override
    public ResponseModel<SearchResponse> scrollByQueryReFields(String index, String type, QueryBuilder queryBuilder, String[] returnFields, int batchSize) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder).fetchSource(returnFields, null);
        searchSourceBuilder.size(batchSize);
        SearchResponse response = transportClient.prepareSearch(index).setTypes(type)
                .addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC)
                .setScroll(TimeValue.timeValueMillis(1000 * 60))
                .setSource(searchSourceBuilder)
                .get();
        return ResponseModel.ok().setBody(response);
    }

    @Override
    public ResponseModel<SearchResponse> scrollByScrollId(String scrollId, long scrollTime) {
        SearchResponse response = transportClient.prepareSearchScroll(scrollId).setScroll(TimeValue.timeValueMillis(scrollTime)).get();
        return ResponseModel.ok().setBody(response);
    }

    @Override
    public ResponseModel<SearchResponse> query(String index, String type, QueryBuilder queryBuilder, QueryBuilder filterBuilder, SortBuilder sortBuilder, AbstractAggregationBuilder aggregation) {
        SearchRequestBuilder searchRequest = transportClient.prepareSearch(index).setTypes(type);
        if (null != queryBuilder && null != filterBuilder) {
            searchRequest.setQuery(queryBuilder).setPostFilter(filterBuilder);
        } else if (null != queryBuilder) {
            searchRequest.setQuery(queryBuilder);
        } else if (null != filterBuilder) {
            searchRequest.setPostFilter(filterBuilder);
        }
        if (null != sortBuilder) {
            searchRequest.addSort(sortBuilder);
        }
        if (null != aggregation) {
            searchRequest.addAggregation(aggregation);
        }
        SearchResponse response = searchRequest.setFrom(SystemContext.getOffset())
                .setSize(SystemContext.getPageSize())
                .get();
        return ResponseModel.ok().setBody(response);
    }

    @Override
    public ResponseModel<SearchResponse> query(String index, String type, QueryBuilder queryBuilder, QueryBuilder filterBuilder, SortBuilder sortBuilder, List<AbstractAggregationBuilder> aggregationList) {
        SearchRequestBuilder searchRequest = transportClient.prepareSearch(index).setTypes(type);
        if (null != queryBuilder && null != filterBuilder) {
            searchRequest.setQuery(queryBuilder).setPostFilter(filterBuilder);
        } else if (null != queryBuilder) {
            searchRequest.setQuery(queryBuilder);
        } else if (null != filterBuilder) {
            searchRequest.setPostFilter(filterBuilder);
        }
        if (null != sortBuilder) {
            searchRequest.addSort(sortBuilder);
        }
        for (AbstractAggregationBuilder aggregation : aggregationList) {
            searchRequest.addAggregation(aggregation);
        }
        SearchResponse response = searchRequest.setFrom(SystemContext.getOffset())
                .setSize(SystemContext.getPageSize()).setPreference("_primary")
                .get();

        MultiSearchRequestBuilder multiSearchRequestBuilder = transportClient.prepareMultiSearch();
        searchRequest.setFrom(SystemContext.getOffset())
                .setSize(SystemContext.getPageSize());
        multiSearchRequestBuilder.add(searchRequest);
        // 执行mutilSearch
        MultiSearchResponse multiSearchResponse = multiSearchRequestBuilder.execute().actionGet();
        MultiSearchResponse.Item[] itemArr = multiSearchResponse.getResponses();

        convertDocList(response);
        return ResponseModel.ok().setBody(itemArr[0].getResponse());
    }

    /**
     * 检查期刊是否有主题分析数据
     *
     * @param jguid
     * @return
     */

}
