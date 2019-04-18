package com.wd.cloud.crsserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.json.JSONUtil;
import com.wd.cloud.crsserver.pojo.document.Oafind;
import com.wd.cloud.crsserver.repository.OafindRepository;
import com.wd.cloud.crsserver.repository.WdSubjectRepository;
import com.wd.cloud.crsserver.service.OafindService;
import com.weidu.commons.search.SearchField;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * @Author: He Zhigang
 * @Date: 2019/4/11 9:51
 * @Description:
 */
@Service("oafindService")
public class OafindServiceImpl implements OafindService {

    @Autowired
    OafindRepository oafindRepository;

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    WdSubjectRepository wdSubjectRepository;

    @Override
    public AggregatedPage<Oafind> baseSearch(String queryStr,
                                             Integer startYear, Integer endYear,
                                             List<Integer> codes,
                                             List<String> sources,
                                             List<Integer> years,
                                             List<String> journals,
                                             List<Integer> languages,
                                             String aggFiled,
                                             Pageable pageable) {
        AggregatedPage<Oafind> oafinds = elasticsearchTemplate.queryForPage(
                OafindRepository.Template.builders(
                        queryStr, startYear, endYear,
                        codes, sources, years, journals, languages,
                        aggFiled, pageable),
                Oafind.class,
                builderHighlightFields());
        Console.log(JSONUtil.parseObj(oafinds, true));
        return oafinds;
    }


    @Override
    public AggregatedPage<Oafind> exactSearch(List<SearchField> searchField, Pageable pageable) {
        return elasticsearchTemplate.queryForPage(
                OafindRepository.Template.exactQuery(searchField, pageable),
                Oafind.class,
                builderHighlightFields());
    }


    private SearchResultMapper builderHighlightFields() {
        return new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> aClass, Pageable pageable) {
                List<Oafind> chunk = new ArrayList<>();
                for (SearchHit searchHit : response.getHits()) {
                    if (response.getHits().getHits().length <= 0) {
                        return null;
                    }
                    Oafind oafind = BeanUtil.toBean(searchHit.getSourceAsMap(), Oafind.class);
                    searchHit.getHighlightFields().forEach((fieldName, highlightField) ->
                            BeanUtil.setFieldValue(oafind, fieldName, highlightField.fragments()[0].toString()));
                    chunk.add(oafind);
                }
                Aggregations aggregations = response.getAggregations();
                return new AggregatedPageImpl<>((List<T>) chunk, pageable, response.getHits().totalHits, aggregations);

            }
        };
    }

}
