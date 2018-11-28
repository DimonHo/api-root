package org.bse.server.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.bse.server.repository.TransportRepository;
import org.bse.server.service.BseService;
import org.bse.server.util.FacetBuildUtil;
import org.bse.server.util.QueryBuilderUtil;
import org.bse.server.util.SortBuildUtil;
import org.bse.server.util.UrlFieldConvert;
import org.bse.server.vo.QueryCondition;
import org.bse.server.vo.SearchCondition;
import org.bse.server.vo.SortCondition;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class BseServiceImpl implements BseService {
	
	@Autowired
	TransportRepository transportRepository;
	
	@Autowired
	UrlFieldConvert urlFieldConvertor;

	@Override
	public List<String> search(List<QueryCondition> list) {
		QueryBuilder queryBuilder = QueryBuilderUtil.convertQueryBuilder(list);
//		SortBuilder sort = SortBuilders.fieldSort("documents.year").order(SortOrder.ASC);
		SortBuilder sort = SortBuildUtil.buildSorts(new SortCondition("timeSort","documents.year","documents",2));
		SearchResponse searchResponse = transportRepository.query(queryBuilder, null, null,sort, "paper",0,5000);
		return transform(searchResponse);
	}
	
	
	
	public List<String> transform(SearchResponse searchResponse) {
		List<String> result = new ArrayList<>();
		SearchHits searchHits = searchResponse.getHits();
		for (SearchHit hit : searchHits.getHits()) {
			Map<String, Object> source = hit.getSource();
			List<Map<String, Object>> list = (List<Map<String, Object>>) source.get("documents");
			Map<String, Object> map = new HashMap<>();
			
			Map<Integer,Object> urlMap = new TreeMap<Integer,Object>();
			
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> json = list.get(i);
				// 遍历map中的键
				for (String key : json.keySet()) {
					String value = json.get(key).toString();
					if (key.equals("url")) {
						List<Map<String, Object>> url = (List<Map<String, Object>>) json.get(key);
						String linkUrl = urlFieldConvertor.convert(url);
						if(linkUrl != null) {
							if(linkUrl.contains("wanfangdata")) {
								urlMap.put(1, linkUrl);
							} else if(linkUrl.contains("cqvip")) {
								urlMap.put(2, linkUrl);
							} else if(linkUrl.contains("cnki")){
								urlMap.put(3, linkUrl);
							} else {
								urlMap.put(5, linkUrl);
							}
							
						}
					} else if(key.equals("doi") && StringUtils.isNotEmpty(json.get(key).toString())) {
						String linkUrl =  "http://doi.org/" + json.get(key).toString();
						urlMap.put(4, linkUrl);
					}
					else if (StringUtils.isNotEmpty(value)) {
						map.put(key, value.split("#&#&#")[0]);
					}
				}
			}
			try {
				Iterator<Integer> iter = urlMap.keySet().iterator();
				String url = null;
				String author = "";
				String docTitle = "";
				String journalTitle = "";
				String year = "";
				String number = "";
				String pageRange = "";
				
				
				StringBuffer sb = new StringBuffer();
				
				if(map.containsKey("author")) {
					author = map.get("author").toString();
					sb.append(author);
				}
				while (iter.hasNext()) {
					Integer key = iter.next();
					url = urlMap.get(key).toString();
					System.out.println(key + ":" + urlMap.get(key));
					break;
				}
				if(map.containsKey("docTitle")) {
					docTitle = map.get("docTitle").toString();
					if(url != null) {
						sb.append("<a target=\"_blank\" href=\"" + url + "\">");
						sb.append(docTitle);
						sb.append("</a>");
					} else {
						sb.append(docTitle);
					}
				}
				sb.append("[J].");
				if(map.containsKey("journalTitle")) {
					journalTitle = map.get("journalTitle").toString();
					sb.append(journalTitle + ".");
				}
				if(map.containsKey("year")) {
					year = map.get("year").toString();
					sb.append(year);
				}
				if(map.containsKey("number")) {
					number = map.get("number").toString();
					sb.append("(" + number + ").");
				}
				if(map.containsKey("pageRange")) {
					pageRange = map.get("pageRange").toString();
					sb.append(pageRange);
				}
				
		        while (iter.hasNext()) {
		        	Integer key = iter.next();
		        	url = urlMap.get(key).toString();
		            System.out.println(key + ":" + urlMap.get(key));
		            break;
		        }
//		        String line = "";
//				if(url != null) {
//					line = author + "<a target=\"_blank\" href=\"" + url + "\">" 
//							+ docTitle + "</a>[J]." + journalTitle + "."
//							+ year + "(" + number + ")."
//							+ pageRange;
//				} else {
//					line = author +" "
//							+ docTitle + "[J]." + journalTitle + "."
//							+ year + "(" + number + ")."
//							+ pageRange;
//				}
				if(!StringUtils.isEmpty(journalTitle)) {		//先只放期刊
					result.add(sb.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
				// TODO: handle exception
			}
		}
		return result;
	}
	
	
	
	
	
	
	
	
	
	public void query(SearchCondition condition) {
		
		QueryBuilder queryBuilder = QueryBuilderUtil.convertQueryBuilder(condition.getQueryConditions());
		SortBuilder sort = SortBuildUtil.buildSorts(condition.getSorts());
		List<AggregationBuilder> facets  = FacetBuildUtil.buildFacets();
		SearchResponse searchResponse = transportRepository.query(queryBuilder, null, facets,sort, "paper",condition.getFrom(),condition.getSize());
		
		transformSSSSSS(searchResponse);
//		return transform(searchResponse);
		
	}
	
	
	
	public List<String> transformSSSSSS(SearchResponse searchResponse) {
		List<String> result = new ArrayList<>();
		SearchHits searchHits = searchResponse.getHits();
		for (SearchHit hit : searchHits.getHits()) {
			Map<String, Object> source = hit.getSource();
			List<Map<String, Object>> list = (List<Map<String, Object>>) source.get("documents");
			Map<String, Object> map = new HashMap<>();
			
			Map<Integer,Object> urlMap = new TreeMap<Integer,Object>();
			
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> json = list.get(i);
				// 遍历map中的键
				for (String key : json.keySet()) {
					String value = json.get(key).toString();
					if (key.equals("url")) {
						List<Map<String, Object>> url = (List<Map<String, Object>>) json.get(key);
						String linkUrl = urlFieldConvertor.convert(url);
						if(linkUrl != null) {
							if(linkUrl.contains("wanfangdata")) {
								urlMap.put(1, linkUrl);
							} else if(linkUrl.contains("cqvip")) {
								urlMap.put(2, linkUrl);
							} else if(linkUrl.contains("cnki")){
								urlMap.put(3, linkUrl);
							} else {
								urlMap.put(5, linkUrl);
							}
							
						}
					} else if(key.equals("doi") && StringUtils.isNotEmpty(json.get(key).toString())) {
						String linkUrl =  "http://doi.org/" + json.get(key).toString();
						urlMap.put(4, linkUrl);
					}
					else if (StringUtils.isNotEmpty(value)) {
						map.put(key, value.split("#&#&#")[0]);
					}
				}
			}
			try {
				Iterator<Integer> iter = urlMap.keySet().iterator();
				String url = null;
				String author = "";
				String docTitle = "";
				String journalTitle = "";
				String year = "";
				String number = "";
				String pageRange = "";
				
				
				StringBuffer sb = new StringBuffer();
				
				if(map.containsKey("author")) {
					author = map.get("author").toString();
					sb.append(author);
				}
				while (iter.hasNext()) {
					Integer key = iter.next();
					url = urlMap.get(key).toString();
					System.out.println(key + ":" + urlMap.get(key));
					break;
				}
				if(map.containsKey("docTitle")) {
					docTitle = map.get("docTitle").toString();
					if(url != null) {
						sb.append("<a target=\"_blank\" href=\"" + url + "\">");
						sb.append(docTitle);
						sb.append("</a>");
					} else {
						sb.append(docTitle);
					}
				}
				sb.append("[J].");
				if(map.containsKey("journalTitle")) {
					journalTitle = map.get("journalTitle").toString();
					sb.append(journalTitle + ".");
				}
				if(map.containsKey("year")) {
					year = map.get("year").toString();
					sb.append(year);
				}
				if(map.containsKey("number")) {
					number = map.get("number").toString();
					sb.append("(" + number + ").");
				}
				if(map.containsKey("pageRange")) {
					pageRange = map.get("pageRange").toString();
					sb.append(pageRange);
				}
				
		        while (iter.hasNext()) {
		        	Integer key = iter.next();
		        	url = urlMap.get(key).toString();
		            System.out.println(key + ":" + urlMap.get(key));
		            break;
		        }
				if(!StringUtils.isEmpty(journalTitle)) {		//先只放期刊
					result.add(sb.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

}
