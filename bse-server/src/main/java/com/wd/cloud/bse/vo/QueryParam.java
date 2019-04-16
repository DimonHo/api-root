package com.wd.cloud.bse.vo;

import com.wd.cloud.bse.service.CacheService;
import com.wd.cloud.bse.util.SpringContextUtil;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.*;

public class QueryParam {

    private JSONObject params;

    private String userAuditStatus;

    private Set<String> esiIssues;

    public QueryParam(JSONObject params) {
        this.params = params;
    }

    public static String getString(JSONObject json, String name) {
        if (!json.has(name)) {
            return null;
        }
        return json.getString(name);
    }

    public static int getInt(JSONObject json, String name, int defaultValue) {
        if (!json.has(name)) {
            return defaultValue;
        }
        return json.getInt(name);
    }

    public static boolean getBool(JSONObject json, String name, boolean defaultValue) {
        if (!json.has(name)) {
            return defaultValue;
        }
        return json.getBoolean(name);
    }

    public String getString(String name) {
        return getString(params, name);
    }

    public void setInt(String name, int value) {
        params.put(name, value);
    }

    public boolean containsKey(String name) {
        return params.containsKey(name);
    }

    public int getInt(String name, int defaultValue) {
        return getInt(params, name, defaultValue);
    }

    public boolean getBool(String name, boolean defaultValue) {
        return getBool(params, name, defaultValue);
    }

    public JSONObject getJSON(String name) {
        String data = this.getString(name);
        return JSONObject.fromObject(data);
    }

    public JSONArray getJSONArray(String name) {
        String data = this.getString(name);
        return JSONArray.fromObject(data);
    }

    public List<QueryCondition> getQueryField() {
        JSONObject queries = null;
        if (!params.has("queries")) {
            return null;
        }
        queries = params.getJSONObject("queries");
        if (queries.has("field")) {
            JSON json = (JSON) queries.get("field");
            return toFields(json);
        }
        return null;
    }

    /**
     * 获取参数ids（如果有id_no[剔除的数据]返回剔除后的数据）
     *
     * @return
     */
    public String[] getIds(List<String> ids) {
//		List<String> ids = new ArrayList<String>();
        if (ids == null) {
            ids = new ArrayList<String>();
        }
        if (params.has("ids")) {
            JSONArray jArr = JSONArray.fromObject(params.getString("ids"));
            for (int i = 0; i < jArr.size(); i++) {
                if (jArr.get(i) instanceof String) {
                    ids.add((String) jArr.get(i));
                } else {
                    JSONObject jObj = jArr.getJSONObject(i);
                    if (jObj.containsKey("id")) {
                        ids.add(jObj.getString("id"));
                    } else if (jObj.containsKey("_id")) {
                        ids.add(jObj.getString("_id"));
                    } else {
                    }
                }
            }
        }
        if (params.has("id_no")) {
            JSONArray jArr = JSONArray.fromObject(params.getString("id_no"));
            for (int i = 0; i < jArr.size(); i++) {
                if (jArr.get(i) instanceof String) {
                    ids.remove((String) jArr.get(i));
                } else {
                    JSONObject jObj = jArr.getJSONObject(i);
                    if (jObj.containsKey("id")) {
                        ids.remove(jObj.getString("id"));
                    } else if (jObj.containsKey("_id")) {
                        ids.remove(jObj.getString("_id"));
                    } else {
                    }
                }
            }
        }
        String[] result = new String[ids.size()];
        return ids.toArray(result);
    }

    private List<QueryCondition> toFields(JSON json) {
        if (json == null) {
            return null;
        }
        List<QueryCondition> list = new ArrayList<QueryCondition>();
        ;
        if (json instanceof JSONArray) {
            JSONArray jArr = (JSONArray) json;
            for (int i = 0; i < jArr.size(); i++) {
                list.add(getField(jArr.getJSONObject(i)));
            }
        } else {
            JSONObject jObj = (JSONObject) json;
            list.add(getField(jObj));
        }
        return list;
    }

    public QueryCondition getField(JSONObject jObj) {
        String fieldName = getString(jObj, "name");
        Object value = null;
        if (jObj.has("value")) {
            value = jObj.get("value");
        }
        int logic = getInt(jObj, "logic", 1);
        QueryCondition condition = new QueryCondition(fieldName);
        condition.setLogic(logic);
        if (value != null) {
            if (value instanceof JSONArray) {
                condition.setValues(JSONArray.toList((JSONArray) value, String.class));
            } else {
                condition.addValue((String) value);
            }
        }
        return condition;
    }

    public List<QueryCondition> getFilterField() {
        JSONObject filters = null;
        if (!params.has("filters")) {
            return null;
        }
        filters = params.getJSONObject("filters");
        if (filters.has("field")) {
            JSON json = (JSON) filters.get("field");
            List<QueryCondition> list = toFields(json);
            for (QueryCondition queryCondition : list) {
                if ("esiIssue".equals(queryCondition.getFieldFlag())) {
                    esiIssues = new HashSet<>();
                    if (queryCondition.getValue() != null) {
                        esiIssues.add(queryCondition.getValue());
                    }
                    if (queryCondition.getValues() != null) {
                        esiIssues.addAll(queryCondition.getValues());
                    }
                }
            }
            return list;
        }
        return null;
    }

    public List<List<QueryCondition>> getFiltersList() {
        JSONObject filters = null;
        List<List<QueryCondition>> list = new ArrayList<>();
        if (!params.has("filter")) {
            return null;
        }
        filters = params.getJSONObject("filter");
        for (Object key : filters.keySet()) {
            String shoulu = key.toString();
            if (filters.get(key) instanceof String) {
                String relationSubject = filters.get(key).toString();
                List<QueryCondition> l = new ArrayList<>();
                l.add(new QueryCondition("relationSubject", relationSubject));
                l.add(new QueryCondition("shoulu", shoulu));
                list.add(l);
            } else {
                JSONArray arr = filters.getJSONArray(key.toString());
                for (int i = 0; i < arr.size(); i++) {
                    String relationSubject = arr.getString(i);
                    List<QueryCondition> l = new ArrayList<>();
                    l.add(new QueryCondition("relationSubject", relationSubject));
                    l.add(new QueryCondition("shoulu", shoulu));
                    list.add(l);
                }
            }
        }
        return list;
    }


//	public static void main(String[] args) throws Exception {
//		String reqParams = "<params><filter>{\"SCI-E\":[\"Toxicology\"],\"EI\":[\"483\",\"631\"]}</filter><types>[1]</types></params>";
//		QueryParam params = ParamsAnalyze.parse(reqParams);
//		params.getFiltersList();
//	}

    /**
     * @return
     */
    public String[] getTypes() {
        List<String> types = new ArrayList<>();
        if (params.has("types")) {
            JSONArray jArr = JSONArray.fromObject(params.getString("types"));
            for (int i = 0; i < jArr.size(); i++) {
//				types.add(jArr.get(i).toString());
                types.add(DocType.valueOf(Integer.parseInt(jArr.get(i).toString())).getValue());
            }
        }
        String[] result = new String[types.size()];
        return types.toArray(result);
    }

    /**
     * @return
     */
    public String[] getTime() {
        List<String> time = new ArrayList<>();
        if (params.has("time")) {
            JSONArray jArr = JSONArray.fromObject(params.getString("time"));
            for (int i = 0; i < jArr.size(); i++) {
                time.add(jArr.get(i).toString());
            }
        }
        String[] result = new String[time.size()];
        return time.toArray(result);
    }

    public String[] getStringArr(String name) {
        List<String> arr = new ArrayList<>();
        if (params.has(name)) {
            JSONArray jArr = JSONArray.fromObject(params.getString(name));
            for (int i = 0; i < jArr.size(); i++) {
                arr.add(jArr.get(i).toString());
            }
        }
        String[] result = new String[arr.size()];
        return arr.toArray(result);
    }

    public SearchCondition converToSearchCondition() {
        SearchCondition condition = new SearchCondition();
        Integer from = getInt("offset", -1);
        condition.setSize(getInt("size", 10));
        condition.setTypes(getTypes());
        Integer isTop = getInt("is_top", 0);
        condition.setIsTop(isTop);
        Integer isFacets = getInt("is_facets", 0);
        condition.setIsFacets(isFacets);
        if (from >= 0) {
        } else {//没有设置offset,取offset
            Integer page = getInt("page", 0);
            if (page == 0) {
                from = 0;
            } else {
                from = (page - 1) * condition.getSize();
            }
        }
        if (isTop == 1 && from >= 100) {
            return null;
        }
        condition.setFrom(from);
        List<QueryCondition> conditions = getQueryField();
        if (conditions != null) {
            condition.setQueryConditions(conditions);
        }
        conditions = getFilterField();
        if (conditions != null) {
            condition.setFilterConditions(conditions);
        }
        List<List<QueryCondition>> filters = getFiltersList();
        condition.setFilters(filters);
        if (conditions != null) {
            List<String> esiIssues = null;
            for (QueryCondition q : conditions) {
                if ("esiIssue".equals(q.getFieldFlag()) && q.getValues() != null && q.getValues().size() > 0) {
                    esiIssues = q.getValues();
                }
            }
            if (esiIssues == null) {
                CacheService cacheService = SpringContextUtil.getBean(CacheService.class);
                String lastEsiIssue = cacheService.getEsiIssue();
                esiIssues = Arrays.asList(lastEsiIssue);
            }
            List<QueryCondition> esiQc = new ArrayList<>();
            Iterator<QueryCondition> ite = conditions.iterator();
            while (ite.hasNext()) {
                QueryCondition q = ite.next();
                if ("shoulu".equals(q.getFieldFlag())) {
                    int logic = q.getLogic();
                    List<String> values = q.getValues();
                    if (values.contains("ESI热点")) {
                        values.remove("ESI热点");
                        List<String> esi = new ArrayList<String>();
                        for (String esiIssue : esiIssues) {
                            esi.add(esiIssue + "^ESI热点");
                        }
                        QueryCondition qc = new QueryCondition("esiIssue", esi, logic);
                        esiQc.add(qc);
                    }
                    if (values.contains("ESI高被引")) {
                        values.remove("ESI高被引");
                        List<String> esi = new ArrayList<String>();
                        for (String esiIssue : esiIssues) {
                            esi.add(esiIssue + "^ESI高被引");
                        }
                        QueryCondition qc = new QueryCondition("esiIssue", esi, logic);
                        esiQc.add(qc);
                    }
                    if (values.size() == 0) {
                        ite.remove();
                    }
                }
            }
            if (esiQc.size() > 0) {
                conditions.addAll(esiQc);
            }
            condition.setFilterConditions(conditions);
        }

//		int order = getInt("sort",0);
        int scid = getInt("school", 0), uid = getInt("uid", 0);
        if (scid > 0) {
            condition.setScid(scid);
        }
        String[] orders = getStringArr("sort");
        for (String order : orders) {
            switch (Integer.parseInt(order)) {
                case 1://时间降序
                    condition.addSort("year", SortEnum.desc.value());
                    break;
                case 2://时间升序
                    condition.addSort("year", SortEnum.asc.value());
                    break;
                case 3://wos被引降序
                    condition.addSort("wosCites", SortEnum.desc.value());
                    break;
                case 4://wos被引升序
                    condition.addSort("wosCites", SortEnum.asc.value());
                    break;
                case 5:
                    condition.addSort("fristWord", SortEnum.desc.value());
                    break;
                case 6:
                    condition.addSort("fristWord", SortEnum.asc.value());
                    break;
                default:
                    if (condition.getQueryConditions() != null && condition.getQueryConditions().size() > 0) {
                    } else {//没有筛选条件，使用默认的排序方式
                        condition.addSort("my_boost_field", SortEnum.desc.value());
                    }
                    break;
            }
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
