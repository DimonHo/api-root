package com.wd.cloud.reportanalysis.service.impl;

import com.wd.cloud.apifeign.FsServerApi;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.reportanalysis.service.DocumentGenerationI;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentGeneration implements DocumentGenerationI {

//    @Autowired
//    private RestTemplate restTemplate;

    @Autowired
    FsServerApi fsServerApi;
    private RestTemplate restTemplate;
    private ClientHttpRequestFactory factory;

    @Autowired
    public void setFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(5000);//ms
        factory.setConnectTimeout(15000);//ms
        this.factory = factory;
        this.restTemplate = new RestTemplate(this.factory);
    }


    @Override
    public JSONObject get(String act, String table, int scid, String compare_scids, String time, String source, int signature) {
        // http://cloud.test.hnlat.com/report-analysis/compare

        MultiValueMap<String, Object> requestEntity = new LinkedMultiValueMap<>();
        requestEntity.add("act", act);
        requestEntity.add("table", table);
        requestEntity.add("scid", scid);
        requestEntity.add("compare_scids", compare_scids);
        requestEntity.add("time", time);
        requestEntity.add("source", source);
        requestEntity.add("signature", signature);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "multipart/form-data");
        headers.set("Accept", "application/json");
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(requestEntity, headers);
        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange("http://cloud.test.hnlat.com/report-analysis/compare", HttpMethod.POST, httpEntity, JSONObject.class);
        return responseEntity.getBody().getJSONObject("body");
    }

    @Override
    public JSONObject getEsi(String table, int scid, String compare_scids, String category_type, int signature) {
        MultiValueMap<String, Object> requestEntity = new LinkedMultiValueMap<>();
        requestEntity.add("act", "esi");
        requestEntity.add("table", table);
        requestEntity.add("scid", scid);
        requestEntity.add("compare_scids", compare_scids);
        requestEntity.add("time", "{\"start\":\"2008\",\"end\":\"2018\"}");
        requestEntity.add("category_type", category_type);
        requestEntity.add("signature", signature);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "multipart/form-data");
        headers.set("Accept", "application/json");
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(requestEntity, headers);
        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange("http://cloud.test.hnlat.com/report-analysis/compare", HttpMethod.POST, httpEntity, JSONObject.class);
        return responseEntity.getBody().getJSONObject("body");
    }

    @Override
    public String input(MultipartFile resource) {
        try {
            MultiValueMap<String, Object> requestEntity = new LinkedMultiValueMap<>();
            requestEntity.add("file", resource);
            requestEntity.add("rename", true);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "multipart/form-data");
            headers.set("Accept", "application/json");
            HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(requestEntity, headers);
            ResponseModel<cn.hutool.json.JSONObject> fileByte = fsServerApi.uploadFile("journalImage", resource);
            fileByte.getBody();
            return fileByte.getBody().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public byte[] downLoad(String unid) {
        ResponseModel<byte[]> fileByte = fsServerApi.getFileByte(unid);
        return fileByte.getBody();
    }


}
