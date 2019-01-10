package com.wd.cloud.bse.service.transfrom.merge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.wd.cloud.bse.util.AuthorUtil;
import com.wd.cloud.bse.util.CommUtil;
import com.weidu.common.util.TextUtils;
import com.wd.cloud.bse.data.Document;
import com.wd.cloud.bse.data.ResourceIndex;

public class AuthorMergeRule extends MergeRule<String> {
	
	private static String spliter = ";";
	
	private static String replace = "#&#&#";
	
	@Override
	protected String name() {
		return "authorMerge";
	}

//	@Override
//	protected String merge(List<String> dataList, ResourceIndex<Document> resource) {
//		boolean validate = false;
//		List<String> authors = new ArrayList<>();
//		for (String string : dataList) {
//			if(StringUtils.isNotEmpty(string)) {
//				authors.addAll(Arrays.asList(string.split(";")));
//			}
//		}
//		return CommUtil.repeatToString(authors);
//	}
	
	
	
	
//	@Override
//	protected String merge(List<String> dataList,ResourceIndex<Document> resource) {
//        Integer docLan = resource.getDocLan();
//        Map<Integer, List<String>> numMap = new HashMap<Integer, List<String>>();
//		List<String> list = new ArrayList<>();	
//		int length = 0;
//		Map<String,String> authorMap = new HashMap<>();
//		String[] authours;
//		for (String data : dataList) {
//			if(data.contains(replace)) {
//				if(docLan == 1) {
//					data = data.split(replace)[0];
//				} else if(docLan == 2) {
//					data = data.split(replace)[1];
//				} else {
//					data = data.replaceAll(replace, spliter);
//				}
//			}
//			authours = data.split(spliter);
//			length = authours.length;
//			if (numMap.containsKey(length)) {
//				list = numMap.get(length);
//			} else {
//				list = new ArrayList<String>();
//				numMap.put(length, list);
//			}
//			list.addAll(Arrays.asList(authours));
//		}
//		
//		if (numMap.size()>0) {
//			List<Entry<Integer, List<String>>> entries = new ArrayList<>(numMap.entrySet());
//			Collections.sort(entries, new Comparator<Entry<Integer, List<String>>>() {
//				@Override
//				public int compare(Entry<Integer, List<String>> o1, Entry<Integer, List<String>> o2) {					
//					return o2.getKey().compareTo(o1.getKey());
//				}
//			});
//			return CommUtil.repeatToString(entries.get(0).getValue());
//		}
//		return "";
//		
//	}
	
	
	
	
	
	@Override
	protected String merge(List<String> dataList, ResourceIndex<Document> resource) {
		boolean validate = false;
//        if (parameters!=null) {
//        	String needValidate = parameters.getOrDefault("needValidate", "0");
//        	if (needValidate.equals("1")) {
//        		validate = true;
//			}
//		}
        Map<String,String> authorMap = new HashMap<>();
        String[] authours;
        int docLan = resource.getDocLan();
//		if (valueMap!=null&&valueMap.size()>0) {
//			String authorValue = (String)valueMap.get("author");
//			docLan = (int)valueMap.get("docLan");
//			if(authorValue != null) {
//				authours = authorValue.split(spliter);
//				for (String authour : authours) {
//					authorMap.put(authour.toLowerCase(), authour);
//				}
//			}
//		}
		
        String str = "";
        Map<Integer, List<String>> numMap = new HashMap<Integer, List<String>>();
		List<String> list;	
		int length = 0;
		for (String data : dataList) {
			if(data.contains(replace)) {
				if(docLan == 1) {
					data = data.split(replace)[0];
				} else if(docLan == 2) {
					data = data.split(replace)[1];
				} else {
					data = data.replaceAll(replace, spliter);
				}
			}
			authours = data.split(spliter);
			if (validate&&!validate(authours)) {
				continue;
			}
			length = authours.length;
			
			if (authorMap.size()>0) {
				Map<String, String> map = alignAuther(data, authorMap);
				if (map.size()==0||map.size()!=length) {
					continue;
				}				
				for (int i=0;i<length;i++) {
					authours[i]=map.get(authours[i]);
				}
				data = String.join(spliter, authours);
			}
			if (numMap.containsKey(length)) {
				list = numMap.get(length);
			}
			else {
				list = new ArrayList<String>();
				numMap.put(length, list);
			}
			list.add(data);
		}
		
		if (numMap.size()>0) {
			List<Entry<Integer, List<String>>> entries = new ArrayList<>(numMap.entrySet());
			Collections.sort(entries, new Comparator<Entry<Integer, List<String>>>() {
				@Override
				public int compare(Entry<Integer, List<String>> o1, Entry<Integer, List<String>> o2) {					
					return o2.getKey().compareTo(o1.getKey());
				}
			});
			str = getAuthor(entries.get(0).getValue());		
		}
		return str;
	}
	
	private boolean validate(String[] authours) {
		boolean bool = true;
		for (String authour : authours) {
			if (authour.length()>50) {
				bool = false;
				break;
			}
		}
		return bool;
	}
	
	private String getAuthor(List<String> dataList) {
		String str = "";		
		int maxLength = Integer.MIN_VALUE, length, 
		maxHanLength = Integer.MIN_VALUE;
		String maxHan = null,han;
		for (String data : dataList) {
			if (TextUtils.hasChineseChar(data)) {
				han = TextUtils.getHans(data);
				length = han.length();
				if (length>maxHanLength) {
					maxHan = data;
					maxHanLength = length;
				}
			}
			length = TextUtils.getLetters(data).length();
			if (length>maxLength) {
				maxLength = length;
				str = data;
			}
		}
		
		if (maxHan!=null) {
			str = maxHan;
		}
		return str;
	}
	
	private Map<String, String> alignAuther(String author,Map<String,String> authorMap) {
		Map<String, String> map = AuthorUtil.alignAuther(author,authorMap.keySet());
		Map<String, String> rMap = new HashMap<>();
		for (Entry<String, String> entry : map.entrySet()) {
			rMap.put(entry.getValue(), authorMap.get(entry.getKey()));
		}
		return rMap;
	}
	
	private String getAuthor(List<String> dataList, String authorValue) {
		Map<Integer, List<String>> numMap = new HashMap<Integer, List<String>>();
	    List<String> list;
		String[] authours;
		int length = 0;
		for (String data : dataList) {
			authours = data.toLowerCase().split(spliter);
			length = containNum(authours, authorValue);
			if (numMap.containsKey(length)) {
				list = numMap.get(length);
			}
			else {
				list = new ArrayList<String>();
				numMap.put(length, list);
			}
			list.add(data);
		}
		
		List<Entry<Integer, List<String>>> entries = new ArrayList<>(numMap.entrySet());
		Collections.sort(entries, new Comparator<Entry<Integer, List<String>>>() {
			@Override
			public int compare(Entry<Integer, List<String>> o1, Entry<Integer, List<String>> o2) {					
				return o2.getKey().compareTo(o1.getKey());
			}
		});
		return getAuthor(entries.get(0).getValue());
	}
	
	private int containNum(String[] authours, String authorValue) {
		int num = 0;
		for (String authour : authours) {
			if (authorValue.contains(authour)) {
				num++;
			}
		}
		return num;
	}
	
		
}
