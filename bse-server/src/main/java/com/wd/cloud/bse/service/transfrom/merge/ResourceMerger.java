package com.wd.cloud.bse.service.transfrom.merge;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wd.cloud.bse.data.Document;
import com.wd.cloud.bse.data.ResourceIndex;
import com.wd.cloud.bse.util.CommUtil;


public class ResourceMerger <T> {
	
	private DefaultSelectRule defaultSelectRule = new DefaultSelectRule();
	
	private static Map<String, PropertyTuple> followedMap = new HashMap<String, PropertyTuple>();
	
	static {
//		displayDetailPropertyList.add(new PropertyTuple("author", "authorMerge", null));
		followedMap.put("shoulu", new PropertyTuple("shoulu", "shouluMerge", null));
		followedMap.put("docLan", new PropertyTuple("docLan", "docLanMerge", null));
		followedMap.put("author", new PropertyTuple("author", "authorMerge", null));
		followedMap.put("year", new PropertyTuple("year", "yearMerge", null));
		followedMap.put("docTitle", new PropertyTuple("docTitle", "stringMultiLangMerge", null));
		followedMap.put("journalTitle", new PropertyTuple("journalTitle", "stringMultiLangMerge", null));
		followedMap.put("description", new PropertyTuple("description", "stringMultiLangMerge", null));
		followedMap.put("keywords", new PropertyTuple("keywords", "stringMultiLangMerge", null));
		followedMap.put("accessionNum", new PropertyTuple("accessionNum", "accessionNumJoinRule", null));
		followedMap.put("wosCites", new PropertyTuple("wosCites", "wosCitesMerge", null));
		followedMap.put("category", new PropertyTuple("category", "categoryMerge", null));
		followedMap.put("subjects", new PropertyTuple("subjects", "subjectsMerge", null));
	}
	
	@SuppressWarnings("unchecked")
	public T merge(ResourceIndex<Document> resource) throws InstantiationException, IllegalAccessException {		
		List<T> docList = (List<T>) resource.getDocuments();
		T doc = docList.get(0);
		if (docList.size()>0) {
			doc = (T)docList.get(0).getClass().newInstance();			
			List<Field> fieldList = new ArrayList<>();	
			getPropertys(doc.getClass(),fieldList);
			for (Field field : fieldList) {
				Object object = null;
				field.setAccessible(true);
				List<T> dataList = getData(field, docList);
//				if(dataList.size() > 0) {
					MergeRule<T> mergeRule = null;
					Map<String, String> parameters = new HashMap<>();
					if (!followedMap.containsKey(field.getName())) {	//默认融合规则
						object = defaultSelectRule.merge(dataList);
					} else {
						mergeRule = MergeRule.getRule(followedMap.get(field.getName()).rule);
						object = mergeRule.merge(dataList, resource);
					}
//				}
				try {
					if (object!=null) {
						field.set(doc,object);					
					}			
				} catch (Exception e) {
				}
			}
		}
		return doc;
	}
	
	private void getPropertys(Class<?> cls, List<Field> fieldList) {
		Field[] fields = cls.getDeclaredFields();
		fieldList.addAll(Arrays.asList(fields));
		if (!cls.getName().equals(Document.class.getName())) {
			getPropertys(cls.getSuperclass(),fieldList);
		}
	}
	
	private List<T> getData(Field field,List<T> docList) {
		List<T> list = new ArrayList<>();
		Object object;
		for (T doc : docList) {
			try {
				object = field.get(doc);
				if (object!=null) {
					list.add((T)object);
				}			
				list = CommUtil.replaceRepeat(list);
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		return list;
	}

}
