package com.wd.cloud.crsserver.service;

import com.weidu.commons.search.SearchCondition;
import com.weidu.commons.search.SearchPager;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.util.List;
import java.util.Map;

/**
 * @Author: He Zhigang
 * @Date: 2019/4/16 21:02
 * @Description:
 */
public interface SearchServcie {

    Map<String,Object> getById(String index,String id);

    Map<String,Map<String,Object>> findByIds(String index, String[] ids);

    void updateById(String index,String type,String id,Map source);

    void index(String index, String type, XContentBuilder source);

    SearchPager<Map<String, Object>> search(SearchCondition searchCondition);
}
