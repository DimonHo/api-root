package com.wd.cloud.bse.es.fliter;

import com.wd.cloud.bse.es.FacetFilterBuilder;
import com.wd.cloud.bse.vo.QueryCondition;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * 通用的词条过滤器
 *
 * @author Administrator
 */
@Component("docLanFilter")
public class DocLanTermsFilterBuildStrategy implements FacetFilterBuilder {

    @Override
    public QueryBuilder build(QueryCondition condition) {
        List<String> vals = condition.getValues();
//		if(vals != null && vals.contains("3")) {
//			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//			for (String val : vals) {
//				BoolQueryBuilder subBoolQueryBuilder = QueryBuilders.boolQuery();
//				if(val.equals("3")) {
//					subBoolQueryBuilder.mustNot(QueryBuilders.termsQuery(condition.getFieldFlag(), new String[] {"1","2"}));
//				} else {
//					subBoolQueryBuilder.must(QueryBuilders.termsQuery(condition.getFieldFlag(), val));
//				}
//				boolQueryBuilder.should(subBoolQueryBuilder);
//			}
//			return boolQueryBuilder;
//		}
        return QueryBuilders.termsQuery(condition.getFieldFlag(), condition.getValues());
    }

    @Override
    public String getPath() {
        return "documents";
    }

}
