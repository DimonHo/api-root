package com.wd.cloud.reportanalysis.service;

import com.wd.cloud.reportanalysis.entity.FacetField;
import com.wd.cloud.reportanalysis.entity.QueryCondition;

import java.util.List;
import java.util.Map;

/**
 * @author He Zhigang
 * @date 2018/5/17
 * @Description:
 */
public interface AnalysisByESServiceI {


//    public Map<String, Object> amount(List<QueryCondition> list, String filed, String type,Map<String,String> facetMap);
	public Map<String, Object> amount(List<QueryCondition> list, FacetField filed, String type);
	
    public Map<String, Object> explain(String type);


}
