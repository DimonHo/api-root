package com.wd.cloud.pdfsearchserver.service.imp;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSON;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.pdfsearchserver.repository.ElasticRepository;
import com.wd.cloud.pdfsearchserver.service.LibgenAllSearchServiceI;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
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
    public ResponseModel<List<JSON>> getResult(String title,String doi) {
        ResponseModel<List<JSON>> responseModel =ResponseModel.fail();
        if(StringUtils.isBlank(title) && StringUtils.isBlank(doi)){
            log.error("标题和doi都为空");
            return responseModel.setMessage("标题和doi不能都为空");
        }
        List<JSON> list = new ArrayList<>();
        BoolQueryBuilder queryBuilder =QueryBuilders.boolQuery();
        if(StringUtils.isNotBlank(title)){
            queryBuilder.must(QueryBuilders.matchQuery("title",title));
        }
        if(StringUtils.isNotBlank(doi)){
            queryBuilder.must(QueryBuilders.termQuery("doi",doi));
        }
        try {
            SearchResponse searchResponse =elasticRepository.queryByName(indexName,type,queryBuilder);
            SearchHits hits =searchResponse.getHits();
            if(hits.getTotalHits()>0){
                Iterator<SearchHit> iterator = hits.iterator();
                JSON json = null;
                while (iterator.hasNext()){
                    SearchHit hit = iterator.next();
                    json=JSON.parseObject(hit.getSourceAsString());
                    list.add(json);
                }
                responseModel=ResponseModel.ok().setBody(list);
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("查询 "+queryBuilder.toString()+" 异常 = "+ e);
        }
        return responseModel;
    }
}
