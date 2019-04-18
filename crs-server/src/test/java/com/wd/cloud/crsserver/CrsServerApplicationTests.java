package com.wd.cloud.crsserver;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Console;
import com.wd.cloud.crsserver.pojo.document.Oafind;
import com.wd.cloud.crsserver.repository.OafindRepository;
import com.wd.cloud.crsserver.service.OafindService;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CrsServerApplicationTests {

    @Autowired
    OafindService oafindService;

    @Autowired
    OafindRepository oafindRepository;

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void contextLoads() {
        Pageable pageable = PageRequest.of(0,3);
        List<Integer> years = CollectionUtil.newArrayList(2013,2014);
        List<Integer> codes = CollectionUtil.newArrayList(121,242);
        List<String> journals = CollectionUtil.newArrayList("BLOOD","FASEB JOURNAL");
        List<String> sources = CollectionUtil.newArrayList("Europe PMC","Citeseer");

        AggregatedPage<Oafind> oafindAggregatedPage = elasticsearchTemplate.queryForPage(OafindRepository.Template.builders("qa",2011,null,null,null,null,null,null,null,pageable), Oafind.class
        ,new SearchResultMapper(){

                    @Override
                    public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> aClass, Pageable pageable) {
                        List<Oafind> chunk = new ArrayList<>();
                        for (SearchHit searchHit : response.getHits()) {
                            if (response.getHits().getHits().length <= 0) {
                                return null;
                            }
                            Oafind oafind = new Oafind();
                            //name or memoe
                            HighlightField title = searchHit.getHighlightFields().get("title");
                            if (title != null) {
                                oafind.setTitle(title.fragments()[0].toString());
                            }
                            HighlightField abstractInfo = searchHit.getHighlightFields().get("abstractInfo");
                            if (abstractInfo != null) {
                                oafind.setAbstractInfo(abstractInfo.fragments()[0].toString());
                            }

                            chunk.add(oafind);
                        }
                        if (chunk.size() > 0) {
                            return new AggregatedPageImpl<>((List<T>) chunk);
                        }
                        return null;
                    }
                });
        Console.log(oafindAggregatedPage.getContent());
    }

}
