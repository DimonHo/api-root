package com.wd.cloud.reportanalysis.repository.analysis;

import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


/**
 * @param <T>
 * @author He Zhigang
 * @date 2018/5/7
 * @Description:
 */
@NoRepositoryBean
public interface AnalysisRepository<T, Long extends Serializable> {

    List<Map<String, Object>> getIssue(int limit_start, int limit_num);

    public Map<String, Object> getanalysisCategory(String column, String issue, String scname, int scid);

    public Map<String, Object> search(int scid, String issue, String category, String classify, String column, int type_c);

    public Integer queryCount(String sql);

    List<Map<String, Object>> query(String string);

    Map<String, Object> queryOne(String string);


    /**
     * 本校ESI学科论文分析
     *
     * @param scid
     * @param issue    期
     * @param category ESI学科类别
     * @param classify
     * @param column
     * @param type_c
     * @return
     */
    public Map<String, Object> searchEsi(int scid, String issue, String category, String classify, String column, int type_c);

}
