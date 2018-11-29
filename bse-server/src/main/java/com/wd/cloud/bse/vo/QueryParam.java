package com.wd.cloud.bse.vo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.wd.cloud.bse.util.SpringContextUtil;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class QueryParam {
	
	private JSONObject params;
	
	private String userAuditStatus;
	
	private Set<String> esiIssues;
	
	public QueryParam(JSONObject params){
		this.params = params;
	}
	
	public String getString(String name){
		return getString(params,name);
	}
	
	public static String getString(JSONObject json,String name){
		if(!json.has(name)){
			return null;
		}
		return json.getString(name);
	}
	
	public void setInt(String name, int value){
		params.put(name, value);
	}
	
	public boolean containsKey(String name){
		return params.containsKey(name);
	}
	
	public int getInt(String name,int defaultValue){
		return getInt(params,name,defaultValue);
	}
	
	public boolean getBool(String name, boolean defaultValue){
		return getBool(params,name,defaultValue);
	}
	
	public static int getInt(JSONObject json,String name,int defaultValue){
		if(!json.has(name)){
			return defaultValue;
		}
		return json.getInt(name);
	}
	
	public static boolean getBool(JSONObject json,String name,boolean defaultValue){
		if(!json.has(name)){
			return defaultValue;
		}
		return json.getBoolean(name);
	}
	
	public JSONObject getJSON(String name){
		String data  = this.getString(name);
		return JSONObject.fromObject(data);
	}
	
	public JSONArray getJSONArray(String name){
		String data = this.getString(name);
		return JSONArray.fromObject(data);
	}
	
	public List<QueryCondition> getQueryField(){
		JSONObject queries = null;
		if(!params.has("queries")){
			return null;
		}
		queries = params.getJSONObject("queries");
		if(queries.has("field")){
			JSON json = (JSON)queries.get("field");
			return toFields(json);
		}
		return null;
	}
	
//	public String[] getIds(){
//		if(params.has("ids")){
//			JSONArray jArr = JSONArray.fromObject(params.getString("ids"));
//			String[] arr = new String[jArr.size()];
//			for(int i=0;i<jArr.size();i++){
//				JSONObject jObj = jArr.getJSONObject(i);
//				if(jObj.containsKey("id")){
//					arr[i] = jObj.getString("id");
//				}else{
//					arr[i] = null;
//				}
//			}
//			return arr;
//		}
//		return new String[0];
//	}
	/**
	 * 获取参数ids（如果有id_no[剔除的数据]返回剔除后的数据）
	 * @return
	 */
	public String[] getIds(List<String> ids){
//		List<String> ids = new ArrayList<String>();
		if(ids == null ) ids = new ArrayList<String>();
		if(params.has("ids")){
			JSONArray jArr = JSONArray.fromObject(params.getString("ids"));
			for(int i=0;i<jArr.size();i++){
				if(jArr.get(i) instanceof String) {
					ids.add((String) jArr.get(i));
				} else {
					JSONObject jObj = jArr.getJSONObject(i);
					if(jObj.containsKey("id")){
						ids.add(jObj.getString("id"));
					}else if (jObj.containsKey("_id")){
						ids.add(jObj.getString("_id"));
					}else{
					}
				}
			}
		}
		if(params.has("id_no")) {
			JSONArray jArr = JSONArray.fromObject(params.getString("id_no"));
			for(int i=0;i<jArr.size();i++){
				if(jArr.get(i) instanceof String) {
					ids.remove((String) jArr.get(i));
				} else {
					JSONObject jObj = jArr.getJSONObject(i);
					if(jObj.containsKey("id")){
						ids.remove(jObj.getString("id"));
					}else if (jObj.containsKey("_id")){
						ids.remove(jObj.getString("_id"));
					}else{
					}
				}
			}
		}
		String[] result  = new String[ids.size()];
		return  ids.toArray(result);
	}
	
	private List<QueryCondition> toFields(JSON json){
		if(json == null) return null;
		List<QueryCondition> list  = new ArrayList<QueryCondition>();;
		if(json instanceof JSONArray){
			JSONArray jArr = (JSONArray)json;
			for(int i =0 ;i<jArr.size();i++){
				list.add(getField(jArr.getJSONObject(i)));
			}
		}else{
			JSONObject jObj =(JSONObject)json;
			list.add(getField(jObj));
		}
		return list;
	}
	
	public QueryCondition getField(JSONObject jObj){
		String fieldName  = getString(jObj,"name");
		Object value = null;
		if(jObj.has("value")){
			value = jObj.get("value");
		}
		int logic = getInt(jObj,"logic",1);
		if(fieldName.equals("missing")){
			if(value != null && value instanceof String){
				if(value.equals("authorAffiliations.claimUsers.uid")){
					fieldName = "cliamUser";
					logic = Logic.AND.value();
				}
				if(value.equals("submitStatus")){//debug 9595
					fieldName = "missingSubmitStatus";
				}
			}
		}
		QueryCondition condition = new QueryCondition(fieldName);
		condition.setLogic(logic);
		if(value != null){
			if(value instanceof JSONArray){
				condition.setValues(JSONArray.toList((JSONArray)value,String.class));
			}else{
				condition.addValue((String)value);
			}
		}
		return condition;
	}
	
	public List<QueryCondition> getFilterField(){
		JSONObject filters = null;
		if(!params.has("filters")){
			return null;
		}
		filters = params.getJSONObject("filters");
		if(filters.has("field")){
			JSON json = (JSON)filters.get("field");
			List<QueryCondition> list = toFields(json);
			for (QueryCondition queryCondition : list) {
				if (queryCondition.getFieldFlag().equals("userAuditStatus")) {
					userAuditStatus = queryCondition.getValue();
				}
				if (queryCondition.getFieldFlag().equals("esiIssue")) {
					esiIssues = new HashSet<>();
					if(queryCondition.getValue()!=null) {
						esiIssues.add(queryCondition.getValue());
					}
					if (queryCondition.getValues()!=null) {
						esiIssues.addAll(queryCondition.getValues());
					}
				}
			}
			return list;
		}
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	public String[] getTypes(){
		List<String> types = new ArrayList<>();
		if(params.has("types")){
			JSONArray jArr = JSONArray.fromObject(params.getString("types"));
			for(int i=0;i<jArr.size();i++){
				types.add(jArr.get(i).toString());
			}
		}
		String[] result  = new String[types.size()];
		return  types.toArray(result);
	}
	
	/**
	 * 
	 * @return
	 */
	public String[] getTime(){
		List<String> time = new ArrayList<>();
		if(params.has("time")){
			JSONArray jArr = JSONArray.fromObject(params.getString("time"));
			for(int i=0;i<jArr.size();i++){
				time.add(jArr.get(i).toString());
			}
		}
		String[] result  = new String[time.size()];
		return  time.toArray(result);
	}
	
	public SearchCondition converToSearchCondition(){
		SearchCondition condition = new SearchCondition();
		Integer from = getInt("offset",-1);
		condition.setSize(getInt("size",10));
		condition.setTypes(getTypes());
		
		if(from >=0){
			
		}else{//没有设置offset,取offset
			Integer page = getInt("page",0);
			if(page ==0){
				from  = 0;
			}else{
				from = (page-1)*condition.getSize();
			}
		}
		condition.setFrom(from);
		List<QueryCondition> conditions = getQueryField();
		if(conditions!=null){
			condition.setQueryConditions(conditions);
		}
		conditions = getFilterField();
//		if(conditions!=null){
//			int logic =Logic.OR.value();
//			List<String> esiIssues = null;
//			for(QueryCondition q : conditions){
//				if("esiIssue".equals(q.getFieldFlag()) && q.getValues()!=null && q.getValues().size()>0){
//					esiIssues = q.getValues();
//				}
//			}
//			if(esiIssues == null){
//				SchoolDao schoolDao = SpringContextUtil.getBean(SchoolDao.class);
//				String lastEsiIssue = schoolDao.getLastEsiIssue();
//				esiIssues = Arrays.asList(lastEsiIssue);
//			}
//			
//			Iterator<QueryCondition> ite = conditions.iterator();
//			List<QueryCondition> esiQc = new ArrayList<>();
//			while(ite.hasNext()){
//				QueryCondition q = ite.next();
//				if("uid".equals(q.getFieldFlag()) || "uid1".equals(q.getFieldFlag())){
//					condition.setUid(Integer.parseInt(q.getValue()));
//				}
//				if("shoulu".equals(q.getFieldFlag())){
//					logic = q.getLogic();
//					List<String> values = q.getValues();
//					if(values.contains("ESI热点")){
//						values.remove("ESI热点");
//						List<String> esi = new ArrayList<String>();
//						for(String esiIssue : esiIssues){
//							esi.add(esiIssue+"^ESI热点");
//						}
//						QueryCondition qc = new QueryCondition("esiWildcardIssue", esi, logic );
//						esiQc.add(qc);
//					}
//					if(values.contains("ESI高被引")){
//						values.remove("ESI高被引");
//						List<String> esi = new ArrayList<String>();
//						for(String esiIssue : esiIssues){
//							esi.add(esiIssue+"^ESI高被引");
//						}
//						QueryCondition qc = new QueryCondition("esiWildcardIssue", esi, logic );
//						esiQc.add(qc);
//					}
//					if(values.size() ==0){
//						ite.remove();
//					}
//				}
//				if("shoulus".equals(q.getFieldFlag())){
//					logic = q.getLogic();
//					List<String> values = q.getValues();
//					if(values.contains("ESI热点")){
//						values.remove("ESI热点");
//						List<String> rightEsi = new ArrayList<String>();
//						for(String esiIssue : esiIssues){
//							rightEsi.add(esiIssue+"^ESI热点");
//						}
//						QueryCondition qc = new QueryCondition("esiWildcardIssue", rightEsi, logic );
//						esiQc.add(qc);
//					}
//					if(values.contains("ESI高被引")){
//						values.remove("ESI高被引");
//						List<String> rightEsi = new ArrayList<String>();
//						for(String esiIssue : esiIssues){
//							rightEsi.add(esiIssue+"^ESI高被引");
//						}
//						QueryCondition qc = new QueryCondition("esiWildcardIssue", rightEsi, logic );
//						esiQc.add(qc);
//					}
//					if(values.size() ==0){
//						ite.remove();
//					}
//				}
//			}
//			if(esiQc.size() > 0) {
//				conditions.addAll(esiQc);
//			}
//			condition.setFilterConditions(conditions);
//		}
		
		
		conditions = getQueryField();
//		if(conditions!=null){
//			int logic =Logic.OR.value();
//			List<String> esiIssues = null;
//			for(QueryCondition q : conditions){
//				if("esiIssue".equals(q.getFieldFlag()) && q.getValues()!=null && q.getValues().size()>0){
//					esiIssues = q.getValues();
//				}
//			}
//			if(esiIssues == null){
//				SchoolDao schoolDao = SpringContextUtil.getBean(SchoolDao.class);
//				String lastEsiIssue = schoolDao.getLastEsiIssue();
//				esiIssues = Arrays.asList(lastEsiIssue);
//			}
//			
//			Iterator<QueryCondition> ite = conditions.iterator();
//			List<QueryCondition> esiQc = new ArrayList<>();
//			while(ite.hasNext()){
//				QueryCondition q = ite.next();
//				if("uid".equals(q.getFieldFlag()) || "uid1".equals(q.getFieldFlag())){
//					condition.setUid(Integer.parseInt(q.getValue()));
//				}
//				if("shoulu".equals(q.getFieldFlag())){
//					logic = q.getLogic();
//					List<String> values = q.getValues();
//					if(values.contains("ESI热点")){
//						values.remove("ESI热点");
//						List<String> esi = new ArrayList<String>();
//						for(String esiIssue : esiIssues){
//							esi.add(esiIssue+"^ESI热点");
//						}
//						QueryCondition qc = new QueryCondition("esiWildcardIssue", esi, logic );
//						esiQc.add(qc);
//					}
//					if(values.contains("ESI高被引")){
//						values.remove("ESI高被引");
//						List<String> esi = new ArrayList<String>();
//						for(String esiIssue : esiIssues){
//							esi.add(esiIssue+"^ESI高被引");
//						}
//						QueryCondition qc = new QueryCondition("esiWildcardIssue", esi, logic );
//						esiQc.add(qc);
//					}
//					if(values.size() ==0){
//						ite.remove();
//					}
//				}
//				if("shoulus".equals(q.getFieldFlag())){
//					logic = q.getLogic();
//					List<String> values = q.getValues();
//					if(values.contains("ESI热点")){
//						values.remove("ESI热点");
//						List<String> rightEsi = new ArrayList<String>();
//						for(String esiIssue : esiIssues){
//							rightEsi.add(esiIssue+"^ESI热点");
//						}
//						QueryCondition qc = new QueryCondition("esiWildcardIssue", rightEsi, logic );
//						esiQc.add(qc);
//					}
//					if(values.contains("ESI高被引")){
//						values.remove("ESI高被引");
//						List<String> rightEsi = new ArrayList<String>();
//						for(String esiIssue : esiIssues){
//							rightEsi.add(esiIssue+"^ESI高被引");
//						}
//						QueryCondition qc = new QueryCondition("esiWildcardIssue", rightEsi, logic );
//						esiQc.add(qc);
//					}
//					if(values.size() ==0){
//						ite.remove();
//					}
//				}
//			}
//			if(esiQc.size() > 0) {
//				conditions.addAll(esiQc);
//			}
//			condition.setQueryConditions(conditions);
//		}
		
		int order = getInt("sort",0);
		int scid = getInt("school",0),uid =getInt("uid",0);
		if(scid>0){
			condition.setScid(scid);
		}
		if(uid>0){
			condition.setUid(uid);
		}
//		boolean isPatent = isPatent(condition);
		switch(order){
		case 1://时间降序
			condition.addSort("timeSort", "documents.year", 2);
			break;
		case 2://时间升序
			condition.addSort("timeSort", "documents.year", 1);
			break;
//		case 3://wos被引降序
//			condition.addSort("wosCitesNested", SortEnum.desc.value());
//			break;
//		case 4://wos被引升序
//			condition.addSort("wosCitesNested", SortEnum.asc.value());
//			break;
//		case 13:
//			condition.addSort("scoreNested", SortEnum.asc.value());
//			break;
//		case 14:
//			condition.addSort("scoreNested", SortEnum.desc.value());
//			break;
//
//		default:
//			if(condition.getQueryConditions()!=null  && condition.getQueryConditions().size()>0){
//			}else{//没有筛选条件，使用默认的排序方式
//				condition.addSort("boostField", SortEnum.desc.value());
//			}
//			break;
		}
		return condition;
	}
	
	
	public String getUserAuditStatus() {
		return userAuditStatus;
	}

	public void setUserAuditStatus(String userAuditStatus) {
		this.userAuditStatus = userAuditStatus;
	}

	public Set<String> getEsiIssues() {
		return esiIssues;
	}

	public void setEsiIssues(Set<String> esiIssues) {
		this.esiIssues = esiIssues;
	}

}
