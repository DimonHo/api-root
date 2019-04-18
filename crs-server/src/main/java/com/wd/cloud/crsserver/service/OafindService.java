package com.wd.cloud.crsserver.service;

import com.wd.cloud.crsserver.pojo.document.Oafind;
import com.weidu.commons.search.SearchField;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;

import java.util.List;

/**
 * @Author: He Zhigang
 * @Date: 2019/4/11 9:51
 * @Description:
 */
public interface OafindService {

    AggregatedPage<Oafind> baseSearch(String queryStr,
                          Integer startYear, Integer endYear,
                          List<Integer> codes,
                          List<String> sources,
                          List<Integer> years,
                          List<String> journals,
                          List<Integer> languages,
                          String aggFiled, Pageable pageable);


    AggregatedPage<Oafind> exactSearch(List<SearchField> searchField, Pageable pageable);
}
