package com.wd.cloud.reportanalysis.util;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Scope("singleton")
public class WebserviceClient {

    private Client client;

    @Value("${spring.datasource.analysis.webserviceUrl}")
    private String webserviceUrl;

    @PostConstruct
    public void init() {
        JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
//		 client = dcf.createClient("http://v3-1.api.hnlat.com/yunscholar/api/instAchievement?wsdl");//线上
        //client = dcf.createClient("http://192.168.1.91:38080/api/instAchievement?wsdl");//线上
        client = dcf.createClient(webserviceUrl);//线上
//	     client = dcf.createClient("http://v3-test.api.hnlat.com/api/instAchievement?wsdl");//测试
//		 client = dcf.createClient("http://localhost:18080/api/instAchievement?wsdl");//本地
    }

    public Client getTransportClient() {
        return client;
    }

}
