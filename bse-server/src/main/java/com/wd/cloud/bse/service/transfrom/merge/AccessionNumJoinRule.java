package com.wd.cloud.bse.service.transfrom.merge;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.wd.cloud.bse.data.Document;
import com.wd.cloud.bse.data.ResourceIndex;

public class AccessionNumJoinRule extends MergeRule <String> {
	
	private static String defaultDelimiter = ";";

	@Override
	protected String name() {
		return "accessionNumJoinRule";
	}
	
	@Override
	protected String merge(List<String> dataList,ResourceIndex<Document> resource) {
		String delimiter = defaultDelimiter;
		String needSplite = "0";
		List<String> list = new ArrayList<>();
		List<String> items;
		for (String data : dataList) {
			if(data.contains("WOS:WOS:")) {
				data = data.replaceFirst("WOS:", "");
			}
			if (!"0".equals(needSplite)) {
				items = split(data);
				for (String item : items) {
					if(!list.contains(item)){
						list.add(item);
					}
				}
			}
			else {
				if(!list.contains(data)){
					list.add(data);
				}
			}
		}
		Collections.sort(list, new Comparator<String>(){
			
			int getWeight(String value){
				if(value.startsWith("WOS")) {
                    return 3;
                }
				if(value.startsWith("EI")) {
                    return 2;
                }
				if(value.startsWith("Medline")) {
                    return 1;
                }
				return 0;
			}

			@Override
			public int compare(String o1, String o2) {
				return (getWeight(o1)-getWeight(o2))>0?-1:1;
			}
			
		});
		return String.join(delimiter, list);
	}
	
	public static List<String> split(String str){
		String[] items = str.split(";");
		List<String> list = new ArrayList<String>();
		String prev = null;
		for(String item : items){
			if(item.contains(":")){
				if(prev != null){
					list.add(prev);
				}
				prev = item;
			}else{
				prev +=";"+item;
			}
		}
		if(prev != null){
			list.add(prev);
		}
		return list;
	}

}
