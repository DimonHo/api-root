package com.wd.cloud.bse.es.query;

import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Component;

import com.wd.cloud.bse.es.QueryBuilderStrategyI;
import com.wd.cloud.bse.util.ChineseUtil;
import com.wd.cloud.bse.util.SimpleUtil;
import com.wd.cloud.bse.vo.LatConstant;
import com.wd.cloud.bse.vo.QueryCondition;


@Component("allFieldQuery")
public class AllFieldQueryStrategy implements QueryBuilderStrategyI {
	
	@Override
	public QueryBuilder execute(QueryCondition queryCondition) {
		String value = queryCondition.getValue();
		String fieldFlag = queryCondition.getFieldFlag();
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolean isFullMatch = false, isName = false;
		
		
		if(value.matches("\\d{4}")){//可能是年
			boolQueryBuilder.should(QueryBuilders.nestedQuery("documents",
					QueryBuilders.termQuery("documents.year",value), ScoreMode.Max).boost(10));
			boolQueryBuilder.should(QueryBuilders.queryStringQuery(
					QueryParser.escape(value.trim())).defaultOperator(Operator.AND).minimumShouldMatch(LatConstant.SEARCH_SIMILARITY_FULLY_MATCH));
		}else if(value.matches("\\d{8,}") || value.matches("(WOS:|EI:|Medline:)\\d+")){//检索入藏号
			boolQueryBuilder.should(QueryBuilders.queryStringQuery(
					QueryParser.escape(value.trim())).defaultOperator(Operator.AND).minimumShouldMatch(LatConstant.SEARCH_SIMILARITY_SITUABLE_MATCH));
		}else if(value.matches("\\b(10[.][0-9]{4,}(?:[.][0-9]+)*/(?:(?![\"&\\'<>])\\S)+)\\b")){//doi
			boolQueryBuilder.should(QueryBuilders.nestedQuery("documents",
						QueryBuilders.termQuery("documents.doi",value), ScoreMode.Max));
		}else if(value.matches("^[\\d]{4}\\-[\\d\\w]{4}$")){//ISSN			
			boolQueryBuilder.should(QueryBuilders.nestedQuery("documents",
					QueryBuilders.termQuery("documents.issn",value), ScoreMode.Max).boost(10));
			boolQueryBuilder.should(QueryBuilders.queryStringQuery(
					QueryParser.escape(value.trim())).defaultOperator(Operator.AND).minimumShouldMatch(LatConstant.SEARCH_SIMILARITY_FULLY_MATCH));
		/*}else if(NLPUtils.isPerson(value)){//如果是人名
			isName = true;
			boolQueryBuilder.should(QueryBuilders.nestedQuery("documents",
					QueryBuilders.queryString(QueryParser.escape(value))
					.defaultField("documents.author").minimumShouldMatch(LatConstant.SEARCH_SIMILARITY_FULLY_MATCH).defaultOperator(Operator.AND).boost(10)
			));
			boolQueryBuilder.should(QueryBuilders.nestedQuery("documents",
					QueryBuilders.queryString(QueryParser.escape(value))
					.defaultField("documents.authorAnalyzed").minimumShouldMatch(LatConstant.SEARCH_SIMILARITY_FULLY_MATCH).defaultOperator(Operator.AND).boost(5)
			));
			if(ChineseUtil.isChinese(value)){//如果是中文名字
				String[] namePys = PinYinUtil.getPinyinNames(value);//获取拼音名称
				BoolQueryBuilder subBoolQueryBuilder = QueryBuilders.boolQuery();
				
				for(String namePy : namePys){
					subBoolQueryBuilder.should(QueryBuilders.nestedQuery("documents",
							QueryBuilders.queryString(QueryParser.escape(namePy))
							.defaultField("documents.author").minimumShouldMatch(LatConstant.SEARCH_SIMILARITY_FULLY_MATCH).defaultOperator(Operator.AND)
							));
					subBoolQueryBuilder.should(QueryBuilders.nestedQuery("documents",
							QueryBuilders.queryString(QueryParser.escape(namePy))
							.defaultField("documents.authorAnalyzed").minimumShouldMatch(LatConstant.SEARCH_SIMILARITY_FULLY_MATCH).defaultOperator(Operator.AND)
							));
				}
				subBoolQueryBuilder.minimumNumberShouldMatch(1);
				boolQueryBuilder.should(subBoolQueryBuilder);
			}
			*/
		}else{
			String[] values = value.trim().split("[，,\\s]+");
			if(values.length == 2 || values.length ==3){//很有可能是姓名
				boolQueryBuilder.should(QueryBuilders.nestedQuery("documents",
						QueryBuilders.queryStringQuery(QueryParser.escape(value))
						.defaultField("documents.author").minimumShouldMatch(LatConstant.SEARCH_SIMILARITY_FULLY_MATCH).defaultOperator(Operator.AND).boost(10)
				, ScoreMode.Max));
				
				/*---------------学位导师*/
				boolQueryBuilder.should(QueryBuilders.nestedQuery("documents",
						QueryBuilders.queryStringQuery(QueryParser.escape(value))
						.defaultField("documents.guider").minimumShouldMatch(LatConstant.SEARCH_SIMILARITY_FULLY_MATCH).defaultOperator(Operator.AND).boost(10)
				, ScoreMode.Max));
			}
			boolQueryBuilder.should(QueryBuilders.queryStringQuery(
					QueryParser.escape(value.trim())).minimumShouldMatch(LatConstant.SEARCH_SIMILARITY_SITUABLE_MATCH));
			boolQueryBuilder.should(QueryBuilders.nestedQuery("documents",
					QueryBuilders.queryStringQuery(QueryParser.escape(value))
					.defaultField("documents.authorAnalyzed").minimumShouldMatch(LatConstant.SEARCH_SIMILARITY_FULLY_MATCH).defaultOperator(Operator.AND).boost(15)
			,ScoreMode.Max));
		}
		
		
		if(ChineseUtil.isChinese(value) && !isName){//如果输入的是中文并且不是名字
			//用户输入了多值
			String[] values = value.trim().split("[，；,;\\s\\t]+");
			if(values.length > 1){
				BoolQueryBuilder subBoolQueryBuilder = QueryBuilders.boolQuery();
				for (String singleVal : values) 
				{
					subBoolQueryBuilder.must(singleWordQuery(fieldFlag, singleVal));
					boolQueryBuilder.should(subBoolQueryBuilder);
				}
			}else{ //单个值情况
				boolQueryBuilder.should(singleWordQuery(fieldFlag, value));
			}
		}
		return boolQueryBuilder;
	}
	
	
	private BoolQueryBuilder singleWordQuery(String fieldFlag, String singleVal )
	{
		BoolQueryBuilder subSubBoolQueryBuilder = QueryBuilders.boolQuery();
		subSubBoolQueryBuilder.should(QueryBuilders.matchPhraseQuery(fieldFlag.trim(),
				QueryParser.escape(singleVal))/*.minimumShouldMatch(LatConstant.SEARCH_SIMILARITY_SITUABLE_MATCH)*/);
		subSubBoolQueryBuilder.should(	
				QueryBuilders.nestedQuery("documents", QueryBuilders.matchPhraseQuery("documents.docTitle",QueryParser.escape(singleVal)),ScoreMode.Max)
		);
		//为机构
		if(SimpleUtil.isOrg(singleVal))
		{
			
				subSubBoolQueryBuilder.should(
						QueryBuilders.nestedQuery("documents", 
						QueryBuilders.matchPhraseQuery("documents.org",QueryParser.escape(singleVal)), ScoreMode.Max));
			
			
		}		
		//判定大部分可能为作者， 作者之间关系 为 且
		if(singleVal.length() < 4)
		{
			
				subSubBoolQueryBuilder.should(QueryBuilders.nestedQuery("documents",
						QueryBuilders.queryStringQuery(QueryParser.escape(singleVal))
						.defaultField("documents.author").minimumShouldMatch(LatConstant.SEARCH_SIMILARITY_FULLY_MATCH).defaultOperator(Operator.AND)
						, ScoreMode.Max));
			
				/*----------------------学位导师*/
				subSubBoolQueryBuilder.should(QueryBuilders.nestedQuery("documents",
						QueryBuilders.queryStringQuery(QueryParser.escape(singleVal))
						.defaultField("documents.guider").minimumShouldMatch(LatConstant.SEARCH_SIMILARITY_FULLY_MATCH).defaultOperator(Operator.AND)
						, ScoreMode.Max));
		}
		//机构的可能性比较大、标题可能性比较大
		else if(singleVal.length() >= 4 && singleVal.length() < 9)
		{
			subSubBoolQueryBuilder.should(QueryBuilders.nestedQuery("documents",
					QueryBuilders.queryStringQuery(QueryParser.escape(singleVal.trim()))
					.defaultField("documents.keywords").minimumShouldMatch(LatConstant.SEARCH_SIMILARITY_FULLY_MATCH).defaultOperator(Operator.AND)
					, ScoreMode.Max));
		}
		else
		{
			subSubBoolQueryBuilder.should(QueryBuilders.nestedQuery("documents", 
					QueryBuilders.matchPhraseQuery("documents.description",QueryParser.escape(singleVal)), ScoreMode.Max));
		}
		return subSubBoolQueryBuilder;
}




}
