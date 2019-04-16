package com.wd.cloud.reportanalysis.util;

import com.wd.cloud.commons.util.StrUtil;
import com.wd.cloud.reportanalysis.entity.FacetField;
import com.wd.cloud.reportanalysis.entity.QueryCondition;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 封装查询条件
 *
 * @author Administrator
 */
public class ResourceLabel {

    private String id;

    private String block;

    private String plate;

    private String act;

    private String table;

    private String scid;

    private String category;

    private String[] compareScids;

    private String[] time;

    private String source;

    private String signature;

    /**
     * 0为按最新年，1为按文章年
     */
    private String type;

    public ResourceLabel(HttpServletRequest request) {
        block = request.getParameter("block");
        plate = request.getParameter("plate");
        act = request.getParameter("act");
        table = request.getParameter("table");
        scid = request.getParameter("scid");
        category = request.getParameter("category_type");
        type = request.getParameter("type");
        if (StrUtil.isEmpty(type)) {
            type = "0";
        }
//		compareScids = request.getParameterValues("compare_scids");
//		time = request.getParameterValues("time");
        String compare = request.getParameter("compareScids");
        String t = request.getParameter("time");
        if (StringUtils.isNotEmpty(compare)) {
            compareScids = compare.replaceAll("\"", "").replaceAll("\\[", "").replaceAll("\\]", "").split(",");
        }
        if (StringUtils.isNotEmpty(t)) {
            JSONObject json = JSONObject.fromObject(t);
            String start = json.getString("start");
            String end = json.getString("end");
            time = new String[]{start, end};
        }
        source = request.getParameter("source");
        signature = request.getParameter("signature");
    }


    public List<String> getScids() {
        List<String> scids = new ArrayList<>();
        scids.add(scid);
        if (compareScids != null) {
            for (String string : compareScids) {
                scids.add(string);
            }
        }
        return scids;
    }

    public String getSource() {
        return source;
    }

    public String getAct() {
        return act;
    }

    /**
     * compare接口查询索引类型
     *
     * @return 索引类型type
     */
    public String getType() {
        String type = "";
        switch (plate) {
            case "esi":
                type = "esi";
                break;
            default:
                type = "resourcelabel";
                break;
        }
//        switch (plate) {
//	        case "esi":
//	            type = "esi";
//	            if(block.equals("ourschool")) {
//	            	type = "analysis";
//	            }
//	            break;
//	        default:
//	            type = "resourcelabel";
//	            break;
//	    }
        return type;
    }


    public String getEsiType() {
        String type = "";
        switch (act) {
            case "amount":
            case "partition":
            case "cited":
                type = "esi";
                break;
            default:
                type = "analysis";
                break;
        }
        return type;
    }


    public List<QueryCondition> getQueryList() {
        List<QueryCondition> list = new ArrayList<>();
        if (time != null) {
            list.add(new QueryCondition("time", Arrays.asList(time)));
        }
        if (source != null) {
            list.add(new QueryCondition("source", source));        //收录
        }
        if (category != null && !"全部领域".equals(category)) {
            list.add(new QueryCondition("category", category));//esi学科
        }
        return list;
    }

    public FacetField getFacetField() {
        FacetField facetField = new FacetField();
        switch (table) {
            case "amount":
                facetField.setName("year");
                facetField.setField("year");
                break;
            case "jcr":
                if ("0".equals(type)) {
                    facetField.setName("jcr_new");
                    facetField.setField("jcr_new");
                } else {
                    facetField.setName("jcr");
                    facetField.setField("jcr");
                }
                break;
            case "jcr_zky_1":
                if ("0".equals(type)) {
                    facetField.setName("jcr_b_new");
                    facetField.setField("jcr_b_new");
                } else {
                    facetField.setName("jcr_b");
                    facetField.setField("jcr_b");
                }
                break;
            case "jcr_zky_2":
                if ("0".equals(type)) {
                    facetField.setName("jcr_s_new");
                    facetField.setField("jcr_s_new");
                } else {
                    facetField.setName("jcr_s");
                    facetField.setField("jcr_s");
                }
                break;
//	        case "jcr_year":
//	        	facetField.setName("jcr_year");
//	        	facetField.setField("jcr");
//	            break;
//	        case "jcr_zky_1_year":
//	        	facetField.setName("jcr_year");
//	        	facetField.setField("jcr_b");
//	            break;
//	        case "jcr_zky_2_year":
//	        	facetField.setName("jcr_year");
//	        	facetField.setField("jcr_s");
//	            break;
            case "total_cited":            //总被引频次
                facetField.setName("wosCitesAll");
                facetField.setField("year");
                break;
            case "paper_cited":            //篇均被引频次
                facetField.setName("wosCites");
                facetField.setField("year");
                break;
            default:
                facetField.setName("year");
                facetField.setField("year");
                break;
        }
        return facetField;
    }


    public Map<String, String> getFacetMap() {
        Map<String, String> facetMap = new HashMap<>();
        switch (table) {
            case "amount":
                facetMap.put("year", "year");
                break;
            case "jcr":
                if ("0".equals(type)) {
                    facetMap.put("jcr_new", "jcr_new");
                } else {
                    facetMap.put("jcr", "jcr");
                }
                break;
            case "jcr_zky_1":
                if ("0".equals(type)) {
                    facetMap.put("jcr_b_new", "jcr_b_new");
                } else {
                    facetMap.put("jcr_b", "jcr_b");
                }
                break;
            case "jcr_zky_2":
                if ("0".equals(type)) {
//            		filed = "jcr_new";
                    facetMap.put("jcr_s_new", "jcr_s_new");
                } else {
                    facetMap.put("jcr_s", "jcr_s");
                }
                break;
            case "jcr_year":
                facetMap.put("jcr_year", "jcr");
                break;
            case "jcr_zky_1_year":
                facetMap.put("jcr_year", "jcr_b");
                break;
            case "jcr_zky_2_year":
                facetMap.put("jcr_year", "jcr_s");
                break;
            case "total_cited":            //总被引频次
                facetMap.put("wosCitesAll", "year");
                break;
            case "paper_cited":            //篇均被引频次
                facetMap.put("wosCites", "year");
                break;
            default:
                facetMap.put("year", "year");
                break;
        }
        return facetMap;
    }


    public String toXML(String scid) {
        StringBuilder xml = new StringBuilder();
        xml.append("<params>");
        if (block != null) {
            xml.append("<block>" + block + "</block>");
        }
        if (plate != null) {
            xml.append("<plate>" + plate + "</plate>");
        }
        if (act != null) {
            xml.append("<act>" + act + "</act>");
        }
        if (scid != null) {
            xml.append("<school>" + scid + "</school>");
        }
        if (time != null) {
            xml.append("<time>[" + time[0] + "," + time[1] + "]</time>");
        }
        if (source != null) {
            xml.append("<source>" + source + "</source>");
        }
        if (signature != null) {
            xml.append("<signature>" + signature + "</signature>");
        }
        if (table != null) {
            xml.append("<table>" + table + "</table>");
        }
        if (type != null) {
            xml.append("<type>" + type + "</type>");
        }
        xml.append("</params>");
        return xml.toString();
    }


    public String getScid() {
        return scid;
    }


    public String getCategory() {
        return category;
    }


    public String[] getCompareScids() {
        return compareScids;
    }


    public String getSignature() {
        return signature;
    }


    public String getBlock() {
        return block;
    }

}
