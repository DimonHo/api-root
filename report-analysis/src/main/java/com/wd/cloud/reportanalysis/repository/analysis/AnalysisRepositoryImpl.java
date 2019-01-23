package com.wd.cloud.reportanalysis.repository.analysis;

import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Repository
public class AnalysisRepositoryImpl<T, ID extends Serializable> implements AnalysisRepository<T, ID> {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 获取最新期
     *
     * @return
     */
    @Transactional
    @Override
    public List<Map<String, Object>> getIssue(int limit_start, int limit_num) {
        String sql = "SELECT issue,esi_issue,wos_issue,rangdate FROM issue ORDER BY issue DESC LIMIT ? ,?";
        Query query = entityManager.createNativeQuery(sql);
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);//按字段名返回map，字段名为key,值为value
        query.setParameter(1, limit_start);
        query.setParameter(2, limit_num);
        List<Map<String, Object>> list = query.getResultList();
//		List<Map<String, Object>> list = queryList(sql,limit_start,limit_num);
        return list;
    }

    @Override
    public Map<String, Object> getanalysisCategory(String column, String issue, String scname, int scid) {
        String sql = "";
        Object[] object = new Object[]{issue, scname};
        if ("category".equals(column)) {
            sql = "SELECT * FROM st_analysis_category WHERE issue = ? and institution_cn = ?";
        } else if ("categoryins".equals(column)) {
            sql = "SELECT * FROM st_analysis_categoryins WHERE issue = ? and institution_cn = ?";
        } else {
            object = new Object[]{issue, scid};
            sql = "SELECT * FROM st_analysis_categoryap WHERE issue = ? and scid = ?";
        }
        Query query = entityManager.createNativeQuery(sql);
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        query.setParameter(1, object[0]);
        query.setParameter(2, object[1]);
        Map<String, Object> map = (Map<String, Object>) query.getSingleResult();
//		Map<String, Object> map = queryMap(sql,object);
        return map;
    }

    /**
     * 获取用户数据：符合sql条件
     *
     * @param scid
     * @return
     */
    @Override
    public Map<String, Object> search(int scid, String issue, String category, String classify, String column, int type_c) {
        String sql = "";
        Object[] object = null;
        switch (classify) {
            case "subject":            //ESI学科分析
                object = new Object[]{issue, scid};
                if ("selected".equals(column)) {
                    sql = "SELECT * FROM st_analysis_subjecta WHERE issue = ? AND scid = ?";
                } else if ("potential".equals(column)) {
                    sql = "SELECT * FROM st_analysis_subjectp WHERE issue = ? AND scid = ?";
                } else {
                    sql = "SELECT * FROM st_analysis_subjectall WHERE issue = ? AND scid = ?";
                }
                break;
            case "competitive":        //ESI学科竞争力分析
                object = new Object[]{issue, category};
                if ("selected".equals(column)) {
                    sql = "SELECT * FROM st_analysis_esi WHERE issue = ? and category = ?";//?
                } else {
                    sql = "SELECT * FROM st_analysis_incites WHERE issue = ? and category = ?";//'物理学'
                }
                break;
            case "level":        //ESI高水平论文分析
                category = "全部领域";
                object = new Object[]{issue, category, scid};
                if ("distribution".equals(column)) {            //
                    object = new Object[]{issue, scid};
                    sql = "SELECT * FROM st_analysis_distribution WHERE issue = ? AND scid = ?";

                } else if ("scale".equals(column)) {
                    sql = "SELECT * FROM st_analysis_year WHERE issue = ? and category = ? and scid = ? and type = 2";

                } else if ("country".equals(column)) {
                    sql = "SELECT * FROM st_analysis_country WHERE issue = ? and category = ? and scid = ? and type = 2";

                } else if ("domestic".equals(column)) {
                    sql = "SELECT * FROM st_analysis_province WHERE issue = ? and category = ? and scid = ? and type = 2";

                } else if ("organ".equals(column)) {
                    sql = "SELECT * FROM st_analysis_org WHERE issue = ? and category = ? and scid = ? and type = 2";

                } else if ("college".equals(column)) {
                    sql = "SELECT * FROM st_analysis_college WHERE issue = ? and category = ? and scid = ? and type = 2";

                } else if ("author".equals(column)) {
                    sql = "SELECT * FROM st_analysis_author WHERE issue = ? and category = ? and scid = ? and type = 2";

                } else if ("journal".equals(column)) {
                    sql = "SELECT * FROM st_analysis_journal WHERE issue = ? and category = ? and scid = ? and type = 2";

                } else if ("fund".equals(column)) {
                    sql = "SELECT * FROM st_analysis_fund WHERE issue = ? and category = ? and scid = ? and type = 2";
                }
                break;
            case "thesis":        //本校ESI论文分析
                category = "全部领域";
            case "paper":        //ESI优势及潜力学科论文分析
                object = new Object[]{issue, category, scid};
                if ("percentile".equals(column)) {            //
                    object = new Object[]{issue, scid};
                    sql = "SELECT * FROM st_analysis_percent WHERE issue = ? and scid = ?";

                } else if ("scale".equals(column)) {
                    sql = "SELECT * FROM st_analysis_year WHERE issue = ? and category = ? and scid = ? and type = 1";

                } else if ("country".equals(column)) {
                    sql = "SELECT * FROM st_analysis_country WHERE issue = ? and category = ? and scid = ? and type = 1";

                } else if ("domestic".equals(column)) {
                    sql = "SELECT * FROM st_analysis_province WHERE issue = ? and category = ? and scid = ? and type = 1";

                } else if ("organ".equals(column)) {
                    sql = "SELECT * FROM st_analysis_org WHERE issue = ? and category = ? and scid = ? and type = 1";

                } else if ("college".equals(column)) {
                    sql = "SELECT * FROM st_analysis_college WHERE issue = ? and category = ? and scid = ? and type = 1";

                } else if ("author".equals(column)) {
                    sql = "SELECT * FROM st_analysis_author WHERE issue = ? and category = ? and scid = ? and type = 1";

                } else if ("journal".equals(column)) {
                    sql = "SELECT * FROM st_analysis_journal WHERE issue = ? and category = ? and scid = ? and type = 1";

                } else if ("fund".equals(column)) {
                    sql = "SELECT * FROM st_analysis_fund WHERE issue = ? and category = ? and scid = ? and type = 1";

                } else if ("classic".equals(column)) {
                    sql = "SELECT * FROM st_analysis_classic WHERE issue = ? and category = ? and scid = ? and type = 1 and type_c = ?";
                    object = new Object[]{issue, category, scid, type_c};
                }
                break;
            default:
                break;
        }

        Query query = entityManager.createNativeQuery(sql);
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        for (int i = 0; i < object.length; i++) {
            query.setParameter(i + 1, object[i]);
        }
//		query.setParameter(1,object[0]);
//		query.setParameter(2,object[1]);
//		if(object.length >2) {
//			query.setParameter(3,object[2]);
//		}
//		List<Map<String, Object>> list = queryList(sql, object);
        Map<String, Object> map = (Map<String, Object>) query.getSingleResult();
        return map;
    }


    /**
     * 统计查询总量
     *
     * @param sql
     * @param args
     * @return
     */
    @Override
    public Integer queryCount(String sql) {
        Query query = entityManager.createNativeQuery(sql);
        String totle = query.getSingleResult().toString();
        return Integer.parseInt(totle);
    }


    @Override
    public List<Map<String, Object>> query(String sql) {
        try {
            Query query = entityManager.createNativeQuery(sql);
            query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            List<Map<String, Object>> list = query.getResultList();
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map<String, Object> queryOne(String sql) {
        try {
            Query query = entityManager.createNativeQuery(sql);
            query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            Map<String, Object> map = (Map<String, Object>) query.getSingleResult();
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}
