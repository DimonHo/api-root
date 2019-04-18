package com.wd.cloud.crsserver.service;

import com.weidu.commons.search.SearchCondition;
import com.weidu.commons.search.SearchPager;

import java.util.Map;

/**
 * @Author: He Zhigang
 * @Date: 2019/4/16 21:02
 * @Description:
 */
public interface SearchServcie {

    SearchPager<Map<String, Object>> search(SearchCondition searchCondition);
}
