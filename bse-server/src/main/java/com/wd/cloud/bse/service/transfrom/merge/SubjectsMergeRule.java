package com.wd.cloud.bse.service.transfrom.merge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import com.wd.cloud.bse.data.Document;
import com.wd.cloud.bse.data.ResourceIndex;

public class SubjectsMergeRule extends MergeRule<String> {
	
	@Override
	protected String name() {
		return "subjectsMerge";
	}

//	@Override
//	protected String merge(List<String> dataList, ResourceIndex<Document> resource) {
//		String subjects = "";
//		Map<String,String> map = new HashMap<>();
//		if(dataList != null) {
//			for (String string : dataList) {
//				if(StringUtils.isNotEmpty(string)) {
//					String key = string.substring(0, string.indexOf(":"));
//					if(map.containsKey(key)) {
//						String val = map.get(key);
//						if(key.contains("EI")) {
//							string = string + " - " + val.replaceAll("EI学科类别:", "");
//						} else {
//							string = string + ";" + val.replaceAll("EI学科类别:", "");
//						}
//					}
////					else {
////						map.put(key, string);
////					}
////					subjects = subjects + ";" + string;
//					map.put(key, string);
//				}
//			}
//		}
//		for (String key : map.keySet()) {
//			String val = map.get(key);
//			if(key.equals("EI学科类别")) {
//				String[] vals = val.split(" - ");
//				Map<String,String> eiMap = new HashMap<>();
//				for (String string : vals) {
//					String eiNum = string.substring(0,string.indexOf(" "));
//					eiMap.put(eiNum, string);
//				}
//			}
//			subjects = subjects + ";" + val;
//		}
//		return subjects.replaceFirst(";", "");
//	}
	/**
	 * map按key排序
	 * @author yangshuaifei
	 *
	 */
	class MapKeyComparator implements Comparator<String>{

	    @Override
	    public int compare(String str1, String str2) {
	        
	        return str1.compareTo(str2);
	    }
	}
	
	@Override
	protected String merge(List<String> dataList, ResourceIndex<Document> resource) {
		String subjects = "";
		Map<String,Object> map = new HashMap<>();
		Map<String,String> eiMap = new TreeMap<>(new MapKeyComparator());
		if(dataList != null) {
			for (String string : dataList) {
				if(StringUtils.isNotEmpty(string)) {
					String key = string.substring(0, string.indexOf(":"));
					String value = string.substring(string.indexOf(":")+1);
					if(key.contains("EI")) {
						String[] vals = value.split(" - ");
						for (String val : vals) {
							String eiNum = val.substring(0,val.indexOf(" "));
							eiMap.put(eiNum, val);
						}
					} else {
//						Set set = new HashSet();
//						if(map.containsKey(key)) {
//							set = (Set) map.get(key);
//						}
//						String[] vals = value.split(";");
//						for (String val : vals) {
//							set.add(val);
//						}
						map.put(key, string);
					}
				}
			}
		}
		for (String key : map.keySet()) {
			String val = (String) map.get(key);
			subjects = subjects + ";" + val;
		}
		if(!eiMap.isEmpty()) {
			String val = "EI学科类别:";
			for (String key : eiMap.keySet()) {
				val = val + " - " + eiMap.get(key);
			}
			subjects = subjects + ";" + val.replaceFirst(" - ", "");
		}
		return subjects.replaceFirst(";", "");
	}
	
	
	
		
}
