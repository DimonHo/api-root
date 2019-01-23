package com.wd.cloud.pdfsearchserver.service.imp;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.pdfsearchserver.model.LiteratureModel;
import com.wd.cloud.pdfsearchserver.repository.ElasticRepository;
import com.wd.cloud.pdfsearchserver.service.pdfSearchServiceI;
import com.wd.cloud.pdfsearchserver.util.StringUtil;
import com.wd.cloud.pdfsearchserver.config.HbaseConfig;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service("pdfSearchService")
public class pdfSearchServiceImp implements pdfSearchServiceI {
    private static final Log log = LogFactory.get(pdfSearchServiceImp.class);
    @Autowired
    ElasticRepository elasticRepository;
    @Autowired
    private HbaseConfig con;
    @Value("${datasource.indexName}")
    private String indexName;
    @Value("${datasource.type}")
    private String type;
    @Value("${datasource.tableName}")
    private String tableName;
    @Value("${datasource.family}")
    private String family;
    @Value("${datasource.column}")
    private String column;

    @Override
    public ResponseModel<String> getRowKey(LiteratureModel literatureModel) {
        ResponseModel<String> responseModel = ResponseModel.fail();
        String rowKey =getRokey(literatureModel);
        log.info("rowKey = "+rowKey);
        if(rowKey!=null){
            responseModel = ResponseModel.ok();
            responseModel.setBody(rowKey);
        }
        return responseModel;
    }

    @Override
    public ResponseModel<byte[]> getpdf(String rowKey) {
        ResponseModel<byte[]> responseModel = ResponseModel.fail();
        byte[] file = getValue(tableName,rowKey,family,column);
        if(file!=null){
            //查询hbase;
            responseModel = ResponseModel.ok();
            responseModel.setBody(file);
        }
        return responseModel;
    }

    private byte[] getValue(String tableName,String rowkey,String family,String column){
        Table table = null;
        Connection connection=null;
        byte[] res= null;
        if (StringUtils.isEmpty(tableName) || StringUtils.isEmpty(family)
                || StringUtils.isEmpty(rowkey) || StringUtils.isEmpty(column)) {
            return null;
        }
        try {
            connection = ConnectionFactory.createConnection(con.configuration());
            table = connection.getTable(TableName.valueOf(tableName));
            Get get = new Get(rowkey.getBytes());
            Result result = table.get(get);
            res = result.getValue(Bytes.toBytes(family), Bytes.toBytes(column));
        } catch (IOException e) {
            log.error(e, "文件查询失败", rowkey);
        }finally {
            try {
                if(table!=null){
                    table.close();
                }
                if(connection!=null){
                    connection.close();
                }
            }catch (IOException e) {
                log.error(e, "hbase关闭链接失败");
            }
        }
        return res;
    }

    private String getRokey(LiteratureModel literatureModel){
        String rowKey=null;
        String title = literatureModel.getDocTitle();
        QueryBuilder queryBuilder = QueryBuilders.matchPhraseQuery("title",title);
        log.info("精确查询："+queryBuilder.toString());
        System.out.println();
        SearchResponse searchResponse =elasticRepository.queryByName(indexName,type,queryBuilder);
        SearchHits hits =searchResponse.getHits();
        if(hits.getTotalHits()==1){//标题精确匹配只有一条
            rowKey =hits.getHits()[0].getSource().get("md5").toString();
        }else if(hits.getTotalHits()>1){//标题精确匹配多条
            rowKey =getOneRowKey(Arrays.asList(hits.getHits()),literatureModel);
        }else{
            //标题模糊匹配
            queryBuilder = QueryBuilders.matchQuery("title",title);
            log.info("模糊查询："+queryBuilder.toString());
            hits=elasticRepository.queryByName(indexName,type,queryBuilder).getHits();
            SearchHit[] searchHits=hits.getHits();
            List<SearchHit> list = new ArrayList<>();
            for(SearchHit hit:searchHits){
                String title_es = StringUtil.repalceSymbol(JSON.parseObject(hit.getSourceAsString()).getString("title"));
                String title_map = StringUtil.repalceSymbol(literatureModel.getDocTitle());
                if(title_es.equals(title_map)){
                    list.add(hit);
                }
            }
            if(list.size()==1){
                rowKey =list.get(0).getSource().get("md5").toString();
            }else if(list.size()>1){
                rowKey =getOneRowKey(list,literatureModel);
            }
        }
        return rowKey;
    }

    private String getOneRowKey(List<SearchHit> searchHits, LiteratureModel literatureModel){
        String result = null;
        int matchNum = 0;
        for(SearchHit hit:searchHits){
            int num = 0;
            String json = hit.getSourceAsString();
            JSONObject jsonObject = JSON.parseObject(json);
            String author = jsonObject.getString("author");
            String journal =jsonObject.getString("journal");
            String year =jsonObject.getString("year");
            String volume =jsonObject.getString("volume");
            String issue =jsonObject.getString("issue");
            String doi =jsonObject.getString("doi");
            String doi2 =jsonObject.getString("doi2");
            String issne =jsonObject.getString("issne");
            String issnp =jsonObject.getString("issnp");
            if(!StringUtils.isEmpty(author) && literatureModel.getAuthor()!=null){
                String author_map = literatureModel.getAuthor();
                //做个作者匹配
                if(author.contains(";")){
                    String[] authors = author.split(";");
                    if(author_map.contains(";")){
                        String[] authors_map = author_map.split(";");
                        for(String au:authors){
                            for(String au_map:authors_map){
                                if(StringUtil.repalceSymbol(au).equals(StringUtil.repalceSymbol(au_map))){
                                    num++;
                                }
                            }
                        }
                    }else{
                        for(String au:authors){
                            if(StringUtil.repalceSymbol(au).equals(StringUtil.repalceSymbol(author_map))){
                                num++;
                            }
                        }
                    }
                }else{
                    if(author_map.contains(";")){
                        String[] authors_map = author_map.split(";");
                        for(String au_map:authors_map){
                            if(StringUtil.repalceSymbol(author).equals(StringUtil.repalceSymbol(au_map))){
                                num++;
                            }
                        }
                    }else{
                        if(StringUtil.repalceSymbol(author).equals(StringUtil.repalceSymbol(author_map))){
                            num++;
                        }
                    }
                }
            }
            if(!StringUtils.isEmpty(journal) && literatureModel.getJournal()!=null){
                journal= StringUtil.repalceSymbol(journal);
                String journal_map = StringUtil.repalceSymbol(literatureModel.getJournal());
                if(journal.equals(journal_map)){
                    num++;
                }
            }
            if(matchField(journal,"journal",literatureModel)){
                num++;
            }
            if(matchField(year,"year",literatureModel)){
                num++;
            }
            if(matchField(volume,"volume",literatureModel)){
                num++;
            }
            if(matchField(issue,"issue",literatureModel)){
                num++;
            }
            if(matchField(doi,"doi",literatureModel)){
                num++;
            }
            if(matchField(doi2,"doi",literatureModel)){
                num++;
            }
            if(matchField(issne,"issn",literatureModel)){
                num++;
            }
            if(matchField(issnp,"issn",literatureModel)){
                num++;
            }
            if(num > matchNum){
                matchNum=num;
                result=jsonObject.getString("md5");
            }else if(num>0 && num==matchNum){
                result=null;
            }
        }
        return result;
    }

    private boolean matchField(String field,String key,LiteratureModel literatureModel){
        Map<String,Object> map =BeanUtil.beanToMap(literatureModel);
        if(!StringUtils.isEmpty(field) && map.containsKey(key)){
            String value = map.get(key).toString();
            if(field.equals(value)){
                return true;
            }
        }
        return false;
    }
}
