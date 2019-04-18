package com.wd.cloud.crsserver.config;

import cn.hutool.core.collection.CollectionUtil;
import com.wd.cloud.crsserver.convert.CodeConvert;
import com.wd.cloud.crsserver.convert.LanguageConvert;
import com.wd.cloud.crsserver.convert.ShouluConvert;
import com.wd.cloud.crsserver.strategy.*;
import com.weidu.commons.search.*;
import com.weidu.commons.search.convert.AggsTermsConvert;
import com.weidu.commons.search.filters.CommTermsFilterBuildStrategy;
import com.weidu.commons.search.filters.IntFromFilterBuildStrategy;
import com.weidu.commons.search.filters.IntToFilterBuildStrategy;
import com.weidu.commons.util.MapFascade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: He Zhigang
 * @Date: 2019/4/16 20:18
 * @Description:
 */
@Configuration
public class ContextConfig {

    @Bean
    public BuilderStrategyContext builderStrategyContext(){
        BuilderStrategyContext builderStrategyContext = new BuilderStrategyContext();
        MapFascade<List<String>> highLightFields = new MapFascade<>();
        highLightFields.put("title",CollectionUtil.newArrayList("title"));
        highLightFields.put("all",CollectionUtil.newArrayList("title","abstractInfo"));
        highLightFields.put("doi",CollectionUtil.newArrayList("doi"));

        MapFascade<QueryBuilderStrategy> queryBuilders = new MapFascade<>();
        queryBuilders.put("keywords",new KeywordsQueryStrategy());
        queryBuilders.put("title",new TitleQueryStrategy());
        queryBuilders.put("all",new AllQueryStrategy());
        queryBuilders.put("doi;pmid",new DoiQueryStrategy());
        queryBuilders.put("org",new OrgQueryStrategy());
        queryBuilders.put("author",new AuthorQueryStrategy());
        queryBuilders.put("journal",new JournalQueryStrategy());

        MapFascade<FilterBuilderStrategy> filterBuilders = new MapFascade<>();
        filterBuilders.put("language;year;shoulu;journal.nameFacet;codes;source.name", new CommTermsFilterBuildStrategy());
        filterBuilders.put("yearFrom", new IntFromFilterBuildStrategy());
        filterBuilders.put("yearTo", new IntToFilterBuildStrategy());

        Map<String,List<AggsField>> facetGroups= new HashMap<>();
        facetGroups.put("default",CollectionUtil.newArrayList(
                new AggsField("source.name",15,1),
                new AggsField("year",15, 3),
                new AggsField("shoulu",10,1),
                new AggsField("codes",500,1),
                new AggsField("journal.nameFacet", 15,1),
                new AggsField("language",10,1)));
        facetGroups.put("source", CollectionUtil.newArrayList(new AggsField("source.name",15,1)));
        facetGroups.put("year", CollectionUtil.newArrayList(new AggsField("year",15,3)));
        facetGroups.put("shoulu", CollectionUtil.newArrayList(new AggsField("shoulu"),new AggsField("codes",500,1)));
        facetGroups.put("subject", CollectionUtil.newArrayList(new AggsField("codes",500,1)));
        facetGroups.put("journal", CollectionUtil.newArrayList(new AggsField("journal.nameFacet",15,1)));
        facetGroups.put("language", CollectionUtil.newArrayList(new AggsField("language",10,1)));

        return builderStrategyContext.setHighLightPreTag("<font class='wd_hightlight'>")
                .setHighLightPostTag("</font>")
                .setHighLightFragmentSize(500)
                .setHighLightFields(highLightFields)
                .setQueryBuilders(queryBuilders)
                .setFilterBuilders(filterBuilders)
                .setFacetGroups(facetGroups);
    }


    @Bean
    public CodeConvert codeConvert(){
        return new CodeConvert();
    }

    @Bean
    public SearchContext SearchContext(){
        Map<String, AggsTermsConvert> facetConverts = new HashMap<>();
        facetConverts.put("language", new LanguageConvert());
        facetConverts.put("shoulu", new ShouluConvert());
        facetConverts.put("codes", codeConvert());
        SearchContext searchContext = new SearchContext();
        searchContext.setFacetConverts(facetConverts);
        return searchContext;
    }


}
