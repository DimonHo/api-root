package com.wd.cloud.reportanalysis.service;

import java.util.List;
import java.util.Map;

/**
 * @author He Zhigang
 * @date 2018/5/17
 * @Description:
 */
public interface AnalysisByDBServiceI {

    public List<Map<String, Object>> getIssue(int limit_start, int limit_num);

    public Map<String, Object> getanalysisCategory(String column, String issue, String scname, int scid);

    public Map<String, Object> analysis(int scid, String issue, String category, String classify, String column, int type_c);

    public Map<String, Object> getColumnList(int scid, String issue, String scname);
    
    
    
    
    
    /**
     * 本校ESI学科论文分析
     * @param scid   学校
     * @param category	ESI学科类别
     * @param act		栏目
     * @param type_c
     * @return
     */
    public Map<String, Object> analysisEsiPaper(int scid, String category , String act,String issue, int type_c);

}
