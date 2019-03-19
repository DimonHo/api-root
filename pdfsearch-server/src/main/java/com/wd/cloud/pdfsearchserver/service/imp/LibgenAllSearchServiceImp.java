package com.wd.cloud.pdfsearchserver.service.imp;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSON;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.pdfsearchserver.repository.ElasticRepository;
import com.wd.cloud.pdfsearchserver.service.LibgenAllSearchServiceI;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
@Service("libgenSearchService")
public class LibgenAllSearchServiceImp implements LibgenAllSearchServiceI {
    private static final Log log = LogFactory.get(LibgenAllSearchServiceImp.class);
    @Autowired
    ElasticRepository elasticRepository;
//    @Value("${datasource.indexName}")
//    private String indexName;
    @Value("${datasource.type}")
    private String type;
    private String indexName = "libgen_all";
    @Override
    public ResponseModel<List<JSON>> getResult(String value) {
        ResponseModel<List<JSON>> responseModel =ResponseModel.fail();
        List<JSON> list = new ArrayList<>();
        QueryBuilder queryBuilder = QueryBuilders.termQuery("doi",value);
        try {
            SearchResponse searchResponse =elasticRepository.queryByName(indexName,type,queryBuilder);
            if(searchResponse.getHits().getTotalHits()==0){
                queryBuilder = QueryBuilders.matchQuery("title",value);
                searchResponse =elasticRepository.queryByName(indexName,type,queryBuilder);
            }
            SearchHits hits =searchResponse.getHits();
            Iterator<SearchHit> iterator = hits.iterator();
            JSON json = null;
            while (iterator.hasNext()){
                SearchHit hit = iterator.next();
                json=JSON.parseObject(hit.getSourceAsString());
                list.add(json);
            }
            responseModel=ResponseModel.ok().setBody(list);
        }catch (Exception e){
            e.printStackTrace();
            log.error("查询 "+queryBuilder.toString()+" 异常 = "+ e);
        }
        return responseModel;
    }
}
