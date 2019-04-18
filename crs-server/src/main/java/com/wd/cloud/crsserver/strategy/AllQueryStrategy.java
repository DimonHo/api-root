package com.wd.cloud.crsserver.strategy;

import com.weidu.commons.search.QueryBuilderStrategy;
import com.weidu.commons.search.SearchField;
import org.apache.lucene.queryparser.classic.QueryParserBase;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder.Type;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 检索所有字段
 *
 * @author shenfu
 */
public class AllQueryStrategy implements QueryBuilderStrategy {

    //DOI 格式匹配
    private static final Pattern DOI_REGX = Pattern.compile("^10\\.\\d{4}([\\d\\.]*?)/[\\d\\-\\/\\w\\.]+");

    //ISSN 格式匹配
    private static final Pattern ISSN_REGX = Pattern.compile("^[\\d]{4}\\-[\\d\\w]{4}$");


    @Override
    public QueryBuilder build(SearchField field) throws RuntimeException {
        String value = field.getValue();
        String lv = value.toLowerCase();
        Matcher m = DOI_REGX.matcher(value);
        BoolQueryBuilder defaultQuery = QueryBuilders.boolQuery();
        String[] fields = {"title^2", "abstractInfo", "doi", "journal.name", "journal.nameAbb", "affiliationAnalyzed", "keywords^2", "classic^2", "authorAnalyzed", "authorFacets^10"
        };
//		Jedis jedis = null;
//		try{
//			jedis = jedisFactory.getJedisFromPool();
        // 如果是doi号
        if (m.matches()) {
//				String id = jedis.get(value);
//				if(StringUtils.isNotBlank(id)){
//					return QueryBuilders.idsQuery("periodical").addIds(id);
//				}else{//可能是doi，提高doi匹配
            defaultQuery.should(QueryBuilders.queryStringQuery(QueryParserBase.escape(lv)).field("doi").minimumShouldMatch("80%").boost(10f));
            defaultQuery.should(QueryBuilders.matchPhraseQuery("doi", QueryParserBase.escape(lv)).boost(10f));
//				}
        }
        //判断是刊名
//			try {
//				byte[] bytes = jedis.get(value.toLowerCase().getBytes("UTF-8"));
//				if(bytes != null){
//					defaultQuery.should(QueryBuilders.termQuery("journal.nameFacet", QueryParserBase.escape(value)).boost(100));
//				}
//			} catch (UnsupportedEncodingException e) {
//				LOG.error("不支持的编码类型",e);
//			}
//		}catch(Exception e){
//			LOG.error("Redis异常",e);
//		}finally{
//			if(jedis != null){
//				jedisFactory.releaseJedis(jedis);
//			}
//		}
        // 如果是issn
        m = ISSN_REGX.matcher(value);
        if (m.matches()) {//可能是ISSN/EISSN
            String[] newFields = new String[fields.length + 2];
            System.arraycopy(fields, 0, newFields, 0, fields.length);
            newFields[fields.length] = "journal.issn";
            newFields[fields.length + 1] = "journal.eissn";
            fields = newFields;
            //defaultQuery.should(QueryBuilders.termQuery("journal.issn", value).boost(100));
        } else {
            value = QueryParserBase.escape(field.getValue());
        }
        //可能是机构
        if (lv.contains("univ") || lv.contains("dept") || lv.contains("coll") || lv.contains("inst")) {//可能是机构
            fields[5] = "affiliationAnalyzed^2";//提升机构字段权重
            defaultQuery.should(QueryBuilders.matchPhraseQuery("affiliationAnalyzed", value).boost(10));
        }

        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(value);
        for (String f : fields) {
            if (f.contains("^")) {
                multiMatchQueryBuilder.field(f.split("\\^")[0], Float.parseFloat(f.split("\\^")[1]));
            } else {
                multiMatchQueryBuilder.field(f);
            }
        }
        defaultQuery.must(multiMatchQueryBuilder.type(Type.BEST_FIELDS).minimumShouldMatch("80%").tieBreaker(0.3f));
        return defaultQuery;
    }

}
