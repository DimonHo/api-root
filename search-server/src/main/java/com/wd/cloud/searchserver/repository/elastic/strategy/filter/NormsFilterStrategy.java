package com.wd.cloud.searchserver.repository.elastic.strategy.filter;

import com.wd.cloud.searchserver.repository.elastic.strategy.FilterBuilderStrategyI;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 在线/纸本期刊过滤
 *
 * @author Administrator
 */
@Component("normsFilter")
public class NormsFilterStrategy implements FilterBuilderStrategyI {

    @Override
    public BoolQueryBuilder execute(BoolQueryBuilder boolFilterBuilder, Set<String> valueSet) {
        return boolFilterBuilder.filter(new TermsQueryBuilder("norms", valueSet));
    }

}
