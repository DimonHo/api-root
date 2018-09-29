package com.wd.cloud.reportanalysis.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.wd.cloud.reportanalysis.repository.analysis.AnalysisRepository;
import com.wd.cloud.reportanalysis.service.AnalysisByDBServiceI;
import com.wd.cloud.reportanalysis.util.ConfigUtil;

import cn.hutool.setting.Setting;

@Service
public class AnalysisByDBService implements AnalysisByDBServiceI{
	
	@Autowired
	AnalysisRepository analysisRepository;
	
	@Override
	public List<Map<String, Object>> getIssue(int limit_start, int limit_num){
		return analysisRepository.getIssue(limit_start, limit_num);
	}
	
	@Override
	public Map<String,Object> getanalysisCategory(String column,String issue,String scname,int scid){
		return analysisRepository.getanalysisCategory(column,issue,scname, scid);
	}
	
	@Override
	public Map<String, Object> analysis(int scid,String issue,String category,String classify, String column,int type_c) {
		return analysisRepository.search(scid, issue, category, classify, column,type_c);
	}
	
	
	public static Cache<String, Map<String,Object>> cache = CacheBuilder.newBuilder().maximumSize(1000).expireAfterWrite(1, TimeUnit.HOURS).build();
	
	
	public Map<String,Object> getColumnList(int scid,String issue) {
		try{
			return cache.get(scid + ":" + issue, new Callable<Map<String,Object>>(){

				@Override
				public Map<String,Object> call() throws Exception {
					return columnList(scid, issue);
				}
			
			});
		}catch(Exception e){
			return columnList(scid, issue);
		}
	}
	
	public Map<String,Object> columnList(int scid,String issue) {
		Map<String,Object> map = new HashMap<>();
		Iterator<Setting.Entry<String,String>> it = ConfigUtil.getIterator();
		while(it.hasNext()) {
			Entry<String,String> entry = it.next();
			String key = entry.getKey();
			String[] keyList = key.split("\\.");
			if(keyList.length > 2) {
				String classify = keyList[0];
				String column = keyList[1];
				String value = entry.getValue();
				String sql =  "SELECT count(1) FROM " + value + " WHERE issue = '" + issue + "'";
				if(!classify.equals("competitive")) {
					sql = sql + " AND scid = '" + scid + "'";
				}
				if(ConfigUtil.getStr(classify+".type") != null) {
					if(!column.equals("distribution") && !column.equals("percentile")) {
						sql = sql + " AND type = '" + ConfigUtil.getStr(classify+".type") + "'";
					}
					
				}
				int result = analysisRepository.queryCount(sql);
				if(result > 0) {
					List<String> columns = new ArrayList<>();
					if(map.containsKey(classify)) {
						columns = (List<String>) map.get(classify);
					}
					columns.add(column);
					map.put(classify, columns);
				}
			}
		}
		cache.put(scid + ":" + issue, map);
		return map;
	}

}
