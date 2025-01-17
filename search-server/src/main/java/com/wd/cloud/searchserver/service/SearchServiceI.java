package com.wd.cloud.searchserver.service;

import com.wd.cloud.searchserver.entity.SearchCondition;
import com.wd.cloud.searchserver.entity.SearchResult;

import java.util.Map;


/**
 * @author He Zhigang
 * @date 2018/6/20
 * @Description:
 */
public interface SearchServiceI {


    public SearchResult search(SearchCondition searchCondition);

    public Map<String, Map<String, String>> searchDisciplineSystem(SearchCondition searchCondition);

    public SearchResult searchSubjectSystem(SearchCondition searchCondition);

    public Map<String, Object> getDoc(String id);

}
