package com.wd.cloud.bse.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.wd.cloud.bse.entity.school.University;
import com.wd.cloud.bse.entity.xk.Issue;
import com.wd.cloud.bse.repository.TransportRepository;
import com.wd.cloud.bse.repository.TransportRepository.BulkInterator;
import com.wd.cloud.bse.repository.school.UniversityRepository;
import com.wd.cloud.bse.repository.xk.IssueRepository;
import com.wd.cloud.bse.service.CacheService;
import com.wd.cloud.bse.util.BaseCache;
import com.wd.cloud.bse.vo.RuleInfo;
import com.wd.cloud.bse.vo.SearchCondition;
import com.wd.cloud.bse.vo.SortTools;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import com.wd.cloud.bse.util.ClientFactory;

import com.opensymphony.oscache.base.NeedsRefreshException;


@Service
@Scope("singleton")
public class CacheServiceImpl implements CacheService,InitializingBean{
	
	private BaseCache cache;
	
	@Autowired
	TransportRepository transportRepository;
	
	@Autowired
	IssueRepository issueRepository;
	
	@Autowired
	UniversityRepository universityRepository;
	
	@PostConstruct 
	public void init(){
		//缓存时间24小时
		cache = new BaseCache("es",1000*3600*24);
		initRules();
		initIssue();
		initUniversity();
	}
	
	protected void initRules(){
		SearchCondition condition = new SearchCondition();
		Iterator<SearchHit> ite = transportRepository.query(null, null, null,"yun_datas", "url");
		List<RuleInfo> subList = null;;
		Map<Integer,List<RuleInfo>> ruleMap= new HashMap<Integer,List<RuleInfo>>();
		RuleInfo rule = null;
		Map<String,Object> map = null;
		while(ite.hasNext()){
			map = ite.next().getSource();
			rule = new RuleInfo();
			rule.setDbId((Integer)map.get("dbId"));
			rule.setBookLinkPattern((String)map.get("bookLinkPattern"));
			rule.setDbLinkUrl((String)map.get("dbLinkUrl"));
			rule.setDocLinkPattern((String)map.get("docLinkPattern"));
			rule.setRuleName((String)map.get("ruleName"));
			rule.setNewDocLinkPattern((String)map.get("newDocLinkPattern"));
			if(map.get("ruleName").toString().equals("链接地址")) {
				rule.setRuleName("DOI");
			}
			rule.setRuleOrder((Integer)map.get("ruleOrder"));
			subList = ruleMap.get(rule.getDbId());
			if(subList == null){
				subList = new ArrayList<RuleInfo>();
			}
			subList.add(rule);
			ruleMap.put(rule.getDbId(), subList);
		}
		if(!ruleMap.isEmpty()) {
			cache.put("rules", ruleMap);
		}
	}
	
	
	protected void initIssue(){
		List<Issue> list = issueRepository.findAll(SortTools.basicSort("desc", "issue"));
		if(list != null && list.size()>0) {
			cache.put("issue", list.get(0));
		}
	}
	
	protected void initUniversity(){
		List<University> list = universityRepository.findAll();
		Map<Integer,String> universityMap = new HashMap<>();
		for (University university : list) {
			universityMap.put(university.getId(), university.getName());
		}
		cache.put("university", universityMap);
	}
	
	
	
	@SuppressWarnings("unchecked")
	public Map<Integer,List<RuleInfo>> getRuleMap(){
		Map<Integer,List<RuleInfo>> rules =null;
		try{
			rules = (Map<Integer,List<RuleInfo>>)cache.get("rules");
		}catch(NeedsRefreshException e){//需要刷新缓存
			e.printStackTrace();
			initRules();
			return getRuleMap();
		}
		return rules;
	}
	
	public Issue getIssue() {
		Issue issue = null;
		try{
			issue = (Issue) cache.get("issue");
		}catch(NeedsRefreshException e){//需要刷新缓存
			e.printStackTrace();
			initIssue();
			return getIssue();
		}
		return issue;
	}
	
	public String getEsiIssue() {
		String esiIssue = null;
		try{
			Issue issue = (Issue) cache.get("issue");
			esiIssue = issue.getEsiIssue().replace(".", "");
		}catch(NeedsRefreshException e){//需要刷新缓存
			e.printStackTrace();
			initIssue();
			return getEsiIssue();
		}
		return esiIssue;
	}
	
	public String getSchool(int id) {
		String name = null;
		try{
			Map<Integer,String> universityMap = (Map<Integer,String>) cache.get("university");
			name = universityMap.get(id);
		}catch(NeedsRefreshException e){//需要刷新缓存
			e.printStackTrace();
			initUniversity();
			return getSchool(id);
		}
		return name;
	}
	
	

	@Override
	public void afterPropertiesSet() throws Exception {
		
	}

	@Override
	public void cache(String key, Object value) {
		cache.put(key, value);
	}

	@Override
	public Object getCache(String key) {
		try {
			return cache.get(key);
		} catch (NeedsRefreshException e) {
			return null;
		}
	}

	@Override
	public void flushAll() {
		cache.flushAll();
	}
}
