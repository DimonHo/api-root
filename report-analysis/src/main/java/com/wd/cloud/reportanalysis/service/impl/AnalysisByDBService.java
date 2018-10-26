package com.wd.cloud.reportanalysis.service.impl;

import cn.hutool.setting.Setting;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.wd.cloud.reportanalysis.repository.analysis.AnalysisRepository;
import com.wd.cloud.reportanalysis.service.AnalysisByDBServiceI;
import com.wd.cloud.reportanalysis.util.ConfigUtil;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

@Service
public class AnalysisByDBService implements AnalysisByDBServiceI {

    public static Cache<String, Map<String, Object>> cache = CacheBuilder.newBuilder().maximumSize(1000).build();
    @Autowired
    private AnalysisRepository analysisRepository;

    @Override
    public List<Map<String, Object>> getIssue(int limit_start, int limit_num) {
        return analysisRepository.getIssue(limit_start, limit_num);
    }

    @Override
    public Map<String, Object> getanalysisCategory(String column, String issue, String scname, int scid) {
        return analysisRepository.getanalysisCategory(column, issue, scname, scid);
    }

    @Override
    public Map<String, Object> analysis(int scid, String issue, String category, String classify, String column, int type_c) {
        return analysisRepository.search(scid, issue, category, classify, column, type_c);
    }

    @Override
    public Map<String, Object> getColumnList(int scid, String issue, String scname) {
        try {
            return cache.get(scid + ":" + issue, new Callable<Map<String, Object>>() {

                @Override
                public Map<String, Object> call() throws Exception {
                    return columnList(scid, issue, scname);
                }

            });
        } catch (Exception e) {
            return columnList(scid, issue, scname);
        }
    }

    public Map<String, Object> columnList(int scid, String issue, String scname) {
        Map<String, Object> map = new HashMap<>();
        Iterator<Setting.Entry<String, String>> it = ConfigUtil.getIterator();
        while (it.hasNext()) {
            Entry<String, String> entry = it.next();
            String key = entry.getKey();
            String[] keyList = key.split("\\.");
            if (keyList.length > 2) {
                String classify = keyList[0];
                String column = keyList[1];
                String table = entry.getValue();
                String sql = "SELECT * FROM " + table + " WHERE issue = '" + issue + "'";

                if (!classify.equals("competitive")) {
                    sql = sql + " AND scid = '" + scid + "'";
                } else {
                    if (column.equals("selected")) {
                        sql = "SELECT * FROM st_analysis_category WHERE issue = '" + issue + "' and institution_cn = '" + scname + "'";
                    } else {
                        sql = "SELECT * FROM st_analysis_categoryins WHERE issue = '" + issue + "' and institution_cn = '" + scname + "'";
                    }
                }
                if (ConfigUtil.getStr(classify + ".type") != null) {
                    if (!column.equals("distribution") && !column.equals("percentile")) {
                        sql = sql + " AND type = '" + ConfigUtil.getStr(classify + ".type") + "'";
                    }

                }
                if (classify.equals("level")) {

                    sql = "SELECT * FROM st_analysis_year WHERE issue = '" + issue + "' and category = '全部领域' AND scid = '" + scid + "' and type = 2";
                }
                if (classify.equals("paper")) {
                    if (!column.equals("percentile")) {
                        sql = "SELECT * FROM st_analysis_categoryap WHERE issue = '" + issue + "' and scid = '" + scid + "'";
                    }
                }

                List<Map<String, Object>> result = analysisRepository.query(sql);

                if (handleResult(result, classify)) {
                    List<String> columns = new ArrayList<>();
                    if (map.containsKey(classify)) {
                        columns = (List<String>) map.get(classify);
                    }
                    columns.add(column);
                    map.put(classify, columns);
                }

            }
        }
        cache.put(scid + ":" + issue, map);
        return map;
    }

    public boolean handleResult(List<Map<String, Object>> result, String classify) {
        if (result != null && result.size() > 0) {
            if (classify.equals("level")) {
                String content = result.get(0).get("content").toString();
                Map<String, Object> contentObj = JSONObject.fromObject(content);
                List<Map<String, Object>> contents = (List<Map<String, Object>>) contentObj.get("content");
                int paper_amount_total = 0;
                for (Map<String, Object> map : contents) {
                    int paper_amount = (int) map.get("paper_amount");
                    paper_amount_total += paper_amount;
                }
                if (paper_amount_total >= 50) {
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;
    }

}
