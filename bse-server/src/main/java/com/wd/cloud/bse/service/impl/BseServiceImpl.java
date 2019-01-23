package com.wd.cloud.bse.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.wd.cloud.bse.data.Document;
import com.wd.cloud.bse.repository.TransportRepository;
import com.wd.cloud.bse.service.BseService;
import com.wd.cloud.bse.service.CacheService;
import com.wd.cloud.bse.service.transfrom.ResultTransform;
import com.wd.cloud.bse.vo.QueryCondition;
import com.wd.cloud.bse.vo.SearchCondition;
import com.wd.cloud.bse.vo.SearchPager;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class BseServiceImpl implements BseService {
	
	@Autowired
	TransportRepository transportRepository;
	
	@Autowired
	ResultTransform resultTransform;
	
	@Autowired
	CacheService cacheService;
	
	@Override
	public List<String> searchScholar(SearchCondition condition) {
		SearchResponse searchResponse = transportRepository.query(condition);
		return resultTransform.transformScholar(searchResponse);
	}
	
	@Override
    public SearchPager query(SearchCondition condition) {
		SearchResponse searchResponse = transportRepository.query(condition);
		SearchPager pager = resultTransform.transform(searchResponse,condition);
		return pager;
	}

	@Override
	public SearchPager getDocByIds(String[] ids,String index) {
		SearchResponse searchResponse = transportRepository.queryByIds(ids, index);
		SearchHits searchHits = searchResponse.getHits();
		SearchPager pager = resultTransform.transform(searchResponse,null);
		return pager;
	}
	
	/**
	 * 最新100条等功能
	 * @param condition
	 * @return
	 */
	@Override
    public <T> SearchPager searchNew(SearchCondition condition) {
		int from = condition.getFrom();
		int size = condition.getSize();
		List<QueryCondition> queryConditions = condition.getQueryConditions();
		List<QueryCondition> filterConditions = condition.getFilterConditions();
		condition.setQueryConditions(null);
		condition.setFilterConditions(null);
		condition.setFrom(0);
		condition.setSize(100);
		condition.setIsFacets(1);
		SearchResponse searchResponse = transportRepository.search(null,condition);
		SearchPager pager = resultTransform.transform(searchResponse,null);
		List<String> ids = new ArrayList<>();;
		List<T> ll = pager.getRows();
		for (T doc : ll) {
			ids.add(((Document)doc).getId());
		}
		String[] strings = new String[ids.size()];
		condition.setQueryConditions(queryConditions);
		condition.setFilterConditions(filterConditions);
		condition.setIsFacets(0);
		condition.setFrom(from);
		condition.setSize(size);
		searchResponse = transportRepository.search(ids.toArray(strings),condition);
		pager = resultTransform.transform(searchResponse,condition);
		return pager;
	}
	
	
	@Override
    public <T> SearchPager searchEsiHot(SearchCondition condition) {
		String lastEsiIssue = cacheService.getEsiIssue();
		int from = condition.getFrom();
		int size = condition.getSize();
		List<QueryCondition> queryConditions = condition.getQueryConditions();
		List<QueryCondition> filterConditions = condition.getFilterConditions();
		condition.setQueryConditions(null);
		condition.setFilterConditions(new ArrayList<>());
		condition.addFilterCondition(new QueryCondition("esiIssue",lastEsiIssue + "^ESI热点"));
		condition.setFrom(0);
		condition.setSize(100);
		condition.setIsFacets(1);
		SearchResponse searchResponse = transportRepository.search(null,condition);
		SearchPager pager = resultTransform.transform(searchResponse,null);
		List<String> ids = new ArrayList<>();;
		List<T> ll = pager.getRows();
		for (T doc : ll) {
			ids.add(((Document)doc).getId());
		}
		String[] strings = new String[ids.size()];
		condition.setQueryConditions(queryConditions);
		condition.setFilterConditions(filterConditions);
		condition.setIsFacets(0);
		condition.setFrom(from);
		condition.setSize(size);
		searchResponse = transportRepository.search(ids.toArray(strings),condition);
		pager = resultTransform.transform(searchResponse,condition);
		return pager;
	}
	
	@Override
    public <T> SearchPager searchEsiTop(SearchCondition condition) {
		String lastEsiIssue = cacheService.getEsiIssue();
		int from = condition.getFrom();
		int size = condition.getSize();
		List<QueryCondition> queryConditions = condition.getQueryConditions();
		List<QueryCondition> filterConditions = condition.getFilterConditions();
		condition.setQueryConditions(null);
		condition.setFilterConditions(new ArrayList<>());
		condition.addFilterCondition(new QueryCondition("esiIssue",lastEsiIssue + "^ESI高被引"));
		condition.setFrom(0);
		condition.setSize(100);
		condition.setIsFacets(1);
		SearchResponse searchResponse = transportRepository.search(null,condition);
		SearchPager pager = resultTransform.transform(searchResponse,null);
		List<String> ids = new ArrayList<>();;
		List<T> ll = pager.getRows();
		for (T doc : ll) {
			ids.add(((Document)doc).getId());
		}
		String[] strings = new String[ids.size()];
		condition.setQueryConditions(queryConditions);
		condition.setFilterConditions(filterConditions);
		condition.setIsFacets(0);
		condition.setFrom(from);
		condition.setSize(size);
		searchResponse = transportRepository.search(ids.toArray(strings),condition);
		pager = resultTransform.transform(searchResponse,condition);
		return pager;
	}

}
