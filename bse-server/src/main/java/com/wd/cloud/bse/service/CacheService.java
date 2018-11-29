package com.wd.cloud.bse.service;

import java.util.List;
import java.util.Map;

import com.wd.cloud.bse.vo.RuleInfo;



/**
 * 缓存服务
 * @author Administrator
 *
 */
public interface CacheService {
	
	/**
	 * 获取缓存的URL转换规则
	 * @return
	 */
	public Map<Integer,List<RuleInfo>> getRuleMap();
	
	/**
	 * 缓存数据
	 * @param key
	 * @param value
	 */
	public void cache(String key,Object value);
	
	/**
	 * 获取缓存的数据
	 * @param key
	 * @return
	 */
	public Object getCache(String key);
	
	/**
	 * 清除所有缓存
	 */
	public void flushAll();

}
