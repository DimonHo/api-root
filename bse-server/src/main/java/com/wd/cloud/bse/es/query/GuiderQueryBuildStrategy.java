package com.wd.cloud.bse.es.query;

import java.util.List;

import cn.hutool.core.util.StrUtil;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.join.ScoreMode;
import com.wd.cloud.bse.es.QueryBuilderStrategyI;
import com.wd.cloud.bse.util.PinYinUtil;
import com.wd.cloud.bse.vo.LatConstant;
import com.wd.cloud.bse.vo.QueryCondition;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component("guiderQuery")
public class GuiderQueryBuildStrategy implements QueryBuilderStrategyI {

	public boolean isEnglish(String str) {
		for(int i=0; i<str.length(); i++) {
			char ch = str.charAt(i);
			if((ch < 0x61 && ch > 0x5a) || ch < 0x41 || ch > 0x7a) {
				return false;
			}
		}
		return true;
	}
	
	
	public QueryBuilder build(String fieldFlag, List<String> values, boolean isAccurate) {
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		String[] authors = null,prev;
		for(String value : values){
			prev = authors;
			String[] authorArray = value.trim().split("[；;]+");
			if(prev != null){
				authors = new String[prev.length+ authorArray.length];
				System.arraycopy(prev, 0, authors, 0, prev.length);
				System.arraycopy(authorArray, 0, authors, prev.length, authorArray.length);
			}else{
				authors = authorArray;
			}
		}
		for(String author : authors){
			if(StrUtil.isEmpty(author)) {
				continue;
			}
			boolean english = false;
			if(isEnglish(author)){ 
				english = true;
			}
			if(isAccurate){
				if(english){
					boolQueryBuilder.must(QueryBuilders.queryStringQuery(QueryParser.escape(author))
					.defaultField(fieldFlag.trim()).minimumShouldMatch(LatConstant.SEARCH_SIMILARITY_FULLY_MATCH));
				}else{
					String namePy = PinYinUtil.getPinyinName(author);//获取拼音名称
					BoolQueryBuilder subBoolQueryBuilder = QueryBuilders.boolQuery();
					
					subBoolQueryBuilder.should(QueryBuilders.queryStringQuery(author)
							.defaultField(fieldFlag.trim()).minimumShouldMatch(LatConstant.SEARCH_SIMILARITY_FULLY_MATCH));
					subBoolQueryBuilder.should(QueryBuilders.queryStringQuery(namePy)
							.defaultField(fieldFlag.trim()).minimumShouldMatch(LatConstant.SEARCH_SIMILARITY_FULLY_MATCH));
					subBoolQueryBuilder.minimumShouldMatch("1");
					boolQueryBuilder.must(subBoolQueryBuilder);
				}
			}else{
				if(english){
					BoolQueryBuilder subBoolQueryBuilder = QueryBuilders.boolQuery();
					
					subBoolQueryBuilder.should(QueryBuilders.nestedQuery("documents",
							QueryBuilders.queryStringQuery(QueryParser.escape(author))
							.defaultField("documents.guider").minimumShouldMatch(LatConstant.SEARCH_SIMILARITY_FULLY_MATCH).defaultOperator(Operator.AND)
							, ScoreMode.Max));
					subBoolQueryBuilder.minimumShouldMatch(1);
					boolQueryBuilder.must(subBoolQueryBuilder);
						
					
//					boolQueryBuilder.should(QueryBuilders.nestedQuery("documents",
//							QueryBuilders.queryStringQuery(QueryParser.escape(author))
//							.defaultField("documents.guider").minimumShouldMatch(LatConstant.SEARCH_SIMILARITY_FULLY_MATCH).defaultOperator(Operator.AND)
//							, ScoreMode.Max));
//					boolQueryBuilder.should(QueryBuilders.nestedQuery("documents",
//							QueryBuilders.queryStringQuery(QueryParser.escape(author))
//							.defaultField("documents.guider").minimumShouldMatch(LatConstant.SEARCH_SIMILARITY_FULLY_MATCH).defaultOperator(Operator.AND)
//							, ScoreMode.Max));
					
					boolQueryBuilder.must(subBoolQueryBuilder);
					
				}else{
					String[] namePys = PinYinUtil.getPinyinNames(author);//获取拼音名称
					BoolQueryBuilder subBoolQueryBuilder = QueryBuilders.boolQuery();
					
					for(String namePy : namePys){
						subBoolQueryBuilder.should(QueryBuilders.nestedQuery("documents",
								QueryBuilders.queryStringQuery(QueryParser.escape(namePy))
								.defaultField("documents.guider").minimumShouldMatch(LatConstant.SEARCH_SIMILARITY_FULLY_MATCH).defaultOperator(Operator.AND)
								, ScoreMode.Max));
						//2018-07-18:解决使用not作者时出现的问题
//						boolQueryBuilder.should(QueryBuilders.nestedQuery("recordGroup.record.documents",
//								QueryBuilders.queryStringQuery(QueryParser.escape(namePy))
//								.defaultField("recordGroup.record.documents.author").minimumShouldMatch(LatConstant.SEARCH_SIMILARITY_FULLY_MATCH).defaultOperator(Operator.AND)
//								, ScoreMode.Max));
//						boolQueryBuilder.should(QueryBuilders.nestedQuery("orgRecordList",
//								QueryBuilders.queryStringQuery(QueryParser.escape(namePy))
//								.defaultField("orgRecordList.document.author").minimumShouldMatch(LatConstant.SEARCH_SIMILARITY_FULLY_MATCH).defaultOperator(Operator.AND)
//								, ScoreMode.Max));
					}
					subBoolQueryBuilder.should(QueryBuilders.nestedQuery("documents",
							QueryBuilders.queryStringQuery(QueryParser.escape(author))
							.defaultField("documents.guider").minimumShouldMatch(LatConstant.SEARCH_SIMILARITY_FULLY_MATCH).defaultOperator(Operator.AND)
							, ScoreMode.Max));
					
					subBoolQueryBuilder.minimumShouldMatch(1);
					boolQueryBuilder.must(subBoolQueryBuilder);
					
					/*
					subBoolQueryBuilder.should(QueryBuilders.nestedQuery("orgRecordList", 
							QueryBuilders.matchPhraseQuery("orgRecordList.document.authorAnalyzed",QueryParser.escape(author)))
					).should(QueryBuilders.nestedQuery("recordGroup.record.documents", 
							QueryBuilders.matchPhraseQuery("recordGroup.record.documents.authorAnalyzed",QueryParser.escape(author))));
					subBoolQueryBuilder.should(QueryBuilders.nestedQuery("orgRecordList", 
							QueryBuilders.matchPhraseQuery("orgRecordList.document.authorAnalyzed",QueryParser.escape(namePy)))
					).should(QueryBuilders.nestedQuery("recordGroup.record.documents", 
							QueryBuilders.matchPhraseQuery("recordGroup.record.documents.authorAnalyzed",QueryParser.escape(namePy))));
					subBoolQueryBuilder.minimumNumberShouldMatch(1);
					*/
					
					//2018-07-18:解决使用not作者时出现的问题
//					boolQueryBuilder.should(QueryBuilders.nestedQuery("recordGroup.record.documents",
//							QueryBuilders.queryStringQuery(QueryParser.escape(author))
//							.defaultField("recordGroup.record.documents.author").minimumShouldMatch(LatConstant.SEARCH_SIMILARITY_FULLY_MATCH).defaultOperator(Operator.AND)
//							, ScoreMode.Max));
//					boolQueryBuilder.should(QueryBuilders.nestedQuery("orgRecordList",
//							QueryBuilders.queryStringQuery(QueryParser.escape(author))
//							.defaultField("orgRecordList.document.author").minimumShouldMatch(LatConstant.SEARCH_SIMILARITY_FULLY_MATCH).defaultOperator(Operator.AND)
//							, ScoreMode.Max));
					
					boolQueryBuilder.must(subBoolQueryBuilder);
					
					
					
				}
			}
		}
		return boolQueryBuilder;
		
	}
	

	@Override
	public QueryBuilder execute(QueryCondition queryCondition) {
		return build(queryCondition.getFieldFlag(), queryCondition.getValues(), false);
	}


	
}
