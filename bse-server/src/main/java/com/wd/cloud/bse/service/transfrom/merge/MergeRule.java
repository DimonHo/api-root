package com.wd.cloud.bse.service.transfrom.merge;

import java.util.List;
import java.util.ServiceLoader;

import com.wd.cloud.bse.data.Document;
import com.wd.cloud.bse.data.ResourceIndex;



public abstract class MergeRule <T> {
	
	protected abstract String name();
	
	@SuppressWarnings("rawtypes")
	private static ServiceLoader<MergeRule> loader = ServiceLoader.load(MergeRule.class);
	
//	protected abstract Object merge(List<T> dataList, Map<String, String> parameters);
	protected abstract Object merge(List<T> dataList, ResourceIndex<Document> resource);
	
	
	@SuppressWarnings("rawtypes")
	public static MergeRule getRule(String name) {
		MergeRule instance = null;
		for (MergeRule rule : loader) {
 		     if(rule.name()==name){
				try {
					instance = rule.getClass().newInstance();
				} catch (Exception e) {
				} 
 		     }				
	   }
 	   return instance;
	}

	
}
