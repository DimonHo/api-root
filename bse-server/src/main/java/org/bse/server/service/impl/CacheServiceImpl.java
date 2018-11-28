package org.bse.server.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.bse.server.repository.TransportRepository;
import org.bse.server.service.CacheService;
import org.bse.server.util.BaseCache;
import org.bse.server.vo.RuleInfo;
import org.bse.server.vo.SearchCondition;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.opensymphony.oscache.base.NeedsRefreshException;


@Service
@Scope("singleton")
public class CacheServiceImpl implements CacheService,InitializingBean{
	
	private BaseCache cache;
	
	@Autowired
	TransportRepository transportRepository;
	
	@PostConstruct 
	public void init(){
		//缓存时间24小时
		cache = new BaseCache("es",1000*3600*24);
		initRules();
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
